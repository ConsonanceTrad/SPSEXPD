package pd.items.artifacts;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.QuickSlot;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.BloodAngry;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Regeneration;
import pd.actors.hero.Hero;
import pd.items.Generator;
import pd.items.rings.Ring;
import pd.items.weapon.melee.MeleeWeapon;
import watabou.noosa.Game;
import watabou.utils.Bundle;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public final class SpsChaliceOfBloodTest {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Ring.initGems();
		try {
			testBloodAngryAction();
			testBloodAngryEffectsAndSave();
			testPrickAndCharge();
			testRegenerationAndSources();
			testResources();
			System.out.println("SPS蓄血圣杯通过：血怒、血祭、回血、存档、来源与双语资源均正常。");
		} finally {
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			app.exit();
		}
	}

	private static void testBloodAngryAction() {
		Hero hero = resetHero();
		ChaliceOfBlood chalice = new ChaliceOfBlood();
		hero.belongings.artifact = chalice;
		chalice.level(3);
		check(!chalice.actions(hero).contains(ChaliceOfBlood.AC_BLOODANGRY),
				"三级圣杯错误开放血怒");
		chalice.level(4);
		check(chalice.actions(hero).contains(ChaliceOfBlood.AC_BLOODANGRY),
				"四级圣杯没有开放血怒");

		float before = hero.cooldown();
		chalice.useBloodAngry(hero);
		BloodAngry angry = hero.buff(BloodAngry.class);
		check(chalice.level() == 1, "血怒没有消耗三级圣杯等级");
		check(angry != null && angry.left() == 100f, "血怒没有持续100回合");
		check(close(hero.cooldown() - before, 1f), "血怒没有消耗1回合");

		chalice.level(4);
		chalice.cursed = true;
		check(!chalice.actions(hero).contains(ChaliceOfBlood.AC_BLOODANGRY),
				"诅咒圣杯错误开放血怒");
	}

	private static void testBloodAngryEffectsAndSave() {
		Hero hero = resetHero();
		hero.HT = hero.HP = 90;
		hero.belongings.weapon = new FixedWeapon();
		int normalDamage = hero.damageRoll();
		BloodAngry angry = Buff.affect(hero, BloodAngry.class).set(100f);
		check(normalDamage == 5 && hero.damageRoll() == 7, "血怒攻击倍率不是旧版1.5倍截断值");
		Char runner = new Char() { };
		float normalSpeed = runner.speed();
		Buff.affect(runner, BloodAngry.class).set(100f);
		check(close(runner.speed(), normalSpeed * 1.2f), "血怒速度倍率不是1.2");

		angry.act();
		check(hero.HP == 89 && angry.left() == 98f, "血怒高生命阶段没有逐步降低生命");
		hero.HP = 10;
		angry.act();
		check(hero.HP == 30 && angry.left() == 96f, "血怒低生命阶段没有收束到三分之一生命");

		hero.HP = 90;
		hero.damage(7, new Object());
		check(hero.HP == 84, "血怒受伤减免没有按0.8倍向上取整");

		Bundle bundle = new Bundle();
		angry.set(37.5f).storeInBundle(bundle);
		BloodAngry restored = new BloodAngry();
		restored.restoreFromBundle(bundle);
		check(restored.left() == 37.5f, "血怒剩余时长没有随存档恢复");
	}

	private static void testPrickAndCharge() {
		RecordingHero hero = new RecordingHero();
		Actor.clear();
		Dungeon.quickslot = new QuickSlot();
		Dungeon.hero = hero;
		hero.HT = hero.HP = 100;
		ChaliceOfBlood chalice = new ChaliceOfBlood();
		hero.belongings.artifact = chalice;
		chalice.level(3);
		hero.HP = 36;
		check(!chalice.shouldWarnAboutPrick(hero), "血祭在等于75%生命阈值时错误警告");
		hero.HP = 35;
		check(chalice.shouldWarnAboutPrick(hero), "血祭超过75%生命阈值时没有警告");
		hero.HP = 100;
		float before = hero.cooldown();
		chalice.prick(hero);
		Bleeding bleeding = hero.buff(Bleeding.class);
		check(hero.lastDamage == 18 && hero.lastSource == chalice, "血祭伤害不是2*等级平方");
		check(bleeding != null && bleeding.level() > 0 && bleeding.level() <= 9,
				"血祭没有按等级平方施加流血");
		check(close(hero.cooldown() - before, 3f), "血祭没有消耗3回合");
		check(chalice.level() == 4, "血祭生还后没有升级圣杯");

		hero.HP = 20;
		chalice.charge(hero, 1000f);
		check(hero.HP == 20, "圣杯充能仍在额外直接治疗");
	}

	private static void testRegenerationAndSources() throws Exception {
		check(close(Regeneration.chaliceRegenDelay(0, false), 10f), "零级圣杯回血间隔错误");
		check(close(Regeneration.chaliceRegenDelay(1, false), 8f), "一级圣杯回血间隔错误");
		check(close(Regeneration.chaliceRegenDelay(2, false), 6f), "二级圣杯回血间隔错误");
		check(close(Regeneration.chaliceRegenDelay(3, false), 5f)
				&& close(Regeneration.chaliceRegenDelay(10, false), 5f), "圣杯回血最低间隔不是5回合");
		check(close(Regeneration.chaliceRegenDelay(10, true), 20f), "诅咒圣杯回血间隔不是20回合");
		check(Arrays.asList(Generator.Category.ARTIFACT.classes).contains(ChaliceOfBlood.class),
				"蓄血圣杯没有保留在普通神器池");

		String king = Files.readString(Paths.get("../java/pd/actors/mobs/King.java"),
				StandardCharsets.UTF_8);
		check(king.contains("new ChaliceOfBlood().identify()"), "矮人王亡灵奖励缺少蓄血圣杯");
	}

	private static void testResources() throws Exception {
		String enItems = Files.readString(Paths.get("messages/items/items.properties"), StandardCharsets.UTF_8);
		String zhItems = Files.readString(Paths.get("messages/items/items_zh.properties"), StandardCharsets.UTF_8);
		String enActors = Files.readString(Paths.get("messages/actors/actors.properties"), StandardCharsets.UTF_8);
		String zhActors = Files.readString(Paths.get("messages/actors/actors_zh.properties"), StandardCharsets.UTF_8);
		check(enItems.contains("items.artifacts.chaliceofblood.ac_bloodangry=S-BLOODANGRY")
				&& enItems.contains("Each time you use the chalice it will drain more life energy")
				&& enItems.contains("you can subtly feel the chalice feeding life energy into you. You still want"),
				"英文圣杯资源不是0.9.8原文");
		check(zhItems.contains("items.artifacts.chaliceofblood.ac_bloodangry=耗竭-血怒")
				&& zhItems.contains("要是不够小心，这种行为可以轻易地杀死你")
				&& zhItems.contains("你可以隐约感受到杯子在为你送来生命能量。你还想"),
				"中文圣杯资源不是0.9.8原文");
		check(enActors.contains("actors.buffs.bloodangry.name=Blood Angry")
				&& enActors.contains("It make you stronger. %s turns left."), "英文血怒资源缺失");
		check(zhActors.contains("actors.buffs.bloodangry.name=血怒")
				&& zhActors.contains("剩余的效果时长：%s回合。")
				&& !zhItems.contains("�") && !zhActors.contains("�"), "中文血怒资源乱码或缺失");
	}

	private static Hero resetHero() {
		Actor.clear();
		Dungeon.quickslot = new QuickSlot();
		Hero hero = new Hero();
		Dungeon.hero = hero;
		return hero;
	}

	private static boolean close(float first, float second) {
		return Math.abs(first - second) < 0.0001f;
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private static final class FixedWeapon extends MeleeWeapon {
		@Override public int damageRoll(Char owner) { return 5; }
	}

	private static final class RecordingHero extends Hero {
		int lastDamage;
		Object lastSource;

		@Override public int drRoll() { return 0; }
		@Override public void damage(int damage, Object source) {
			lastDamage = damage;
			lastSource = source;
		}
	}

	private SpsChaliceOfBloodTest() { }
}
