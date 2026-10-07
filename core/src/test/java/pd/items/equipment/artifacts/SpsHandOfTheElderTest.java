package pd.items.equipment.artifacts;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Roots;
import pd.actors.buffs.Slow;
import pd.actors.buffs.Weakness;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.items.Generator;
import pd.items.equipment.rings.RingOfForce;
import pd.items.equipment.rings.RingOfFuror;
import render.noosa.Game;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Headless checks for the ported hand of the elder: ring wearing, pointing and recharge. */
public final class SpsHandOfTheElderTest {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Random.pushGenerator(0x535053454C444552L);
		try {
			testStatsAndActions();
			testWearRing();
			testPointAt();
			testRechargeAndSave();
			testPoolAndMessages();
			System.out.println("SPS古老者之手测试通过：初始充能、镶嵌成长与诅咒、指向伤害/定身/戒指状态、充能恢复、存档、神器池与双语文本均正常。");
		} finally {
			Random.popGenerator();
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			app.exit();
		}
	}

	private static void testStatsAndActions() {
		Hero hero = new Hero();
		Dungeon.hero = hero;
		HandOfTheElder hand = new HandOfTheElder();
		check(hand.levelCap() == 10 && hand.chargeCap() == 2 && hand.charge() == 2,
				"古老者之手初始等级上限或充能错误");
		check(hand.image == pd.atlas.items.SpecificPlaceHolderDict.ARTIFACT_HOLDER_0,
				"古老者之手占位图标错误");
		check(!hand.actions(hero).contains(HandOfTheElder.AC_POINT), "未装备时错误开放指向动作");

		hero.belongings.backpack.items.add(hand);
		check(hand.doEquip(hero), "古老者之手无法装备");
		check(hand.actions(hero).contains(HandOfTheElder.AC_POINT), "装备后没有开放指向动作");
		check(hand.actions(hero).contains(HandOfTheElder.AC_WEAR), "装备后没有开放镶嵌动作");

		hand.charge = 0;
		check(!hand.actions(hero).contains(HandOfTheElder.AC_POINT), "充能为 0 时仍开放指向动作");
	}

	private static void testWearRing() {
		Hero hero = new Hero();
		Dungeon.hero = hero;
		HandOfTheElder hand = new HandOfTheElder();
		hero.belongings.backpack.items.add(hand);
		check(hand.doEquip(hero), "古老者之手无法装备");

		RingOfFuror furor = new RingOfFuror();
		furor.level(2);
		check(hand.wearRing(furor), "古老者之手无法镶嵌戒指");
		check(hand.level() == 3, "镶嵌二级戒指没有让骨手成长 3 级：" + hand.level());
		check(hand.rings().size() == 1, "镶嵌后没有记录戒指种类");
		check(!hand.wearRing(new RingOfFuror()), "同类戒指不该被重复镶嵌");
		check(hand.rings().size() == 1, "重复镶嵌改变了戒指列表");

		//诅咒戒指会诅咒骨手
		RingOfForce force = new RingOfForce();
		force.cursed = true;
		check(hand.wearRing(force), "诅咒戒指无法镶嵌");
		check(hand.cursed && hand.rings().size() == 2, "诅咒戒指没有让骨手被诅咒");

		//镶嵌满 5 枚后不再开放镶嵌动作（换一位英雄，避免同一位英雄身上的同类神器互斥）
		Hero another = new Hero();
		Dungeon.hero = another;
		HandOfTheElder full = new HandOfTheElder();
		another.belongings.backpack.items.add(full);
		check(full.doEquip(another), "镶满测试：古老者之手无法装备");
		for (int i = 0; i < HandOfTheElder.MAX_RINGS; i++) {
			pd.items.equipment.rings.Ring ring = i == 0 ? new RingOfFuror() : distinctRing(i);
			check(full.wearRing(ring), "第 " + (i + 1) + " 枚戒指无法镶嵌");
		}
		check(!full.actions(another).contains(HandOfTheElder.AC_WEAR), "镶满 5 枚后仍开放镶嵌动作");
	}

	private static pd.items.equipment.rings.Ring distinctRing(int index) {
		switch (index) {
			case 1: return new RingOfForce();
			case 2: return new pd.items.equipment.rings.RingOfMight();
			case 3: return new pd.items.equipment.rings.RingOfHaste();
			default: return new pd.items.equipment.rings.RingOfAccuracy();
		}
	}

	private static void testPointAt() {
		Hero hero = new Hero();
		hero.pos = 27;
		Dungeon.hero = hero;
		HandOfTheElder hand = new HandOfTheElder();
		hero.belongings.backpack.items.add(hand);
		check(hand.doEquip(hero), "古老者之手无法装备");
		RingOfFuror furor = new RingOfFuror();
		check(hand.wearRing(furor), "古老者之手无法镶嵌戒指");

		TestMob target = new TestMob(200);
		int before = target.HP;
		hand.pointAt(target);
		check(hand.charge() == 1, "指向没有消耗充能：" + hand.charge());
		check(target.HP < before, "指向没有造成伤害");
		int dealt = before - target.HP;
		check(dealt >= 20 && dealt <= 40, "指向伤害不是目标最大生命的 10%~20%：" + dealt);
		check(target.buff(Roots.class) != null, "指向没有定身目标（无法逃脱）");
		check(target.buff(Slow.class) != null, "指向没有施加戒指对应状态（急速/愤怒 → 迟缓）");
		//镶入 1 枚 0 级戒指后骨手为 1 级：定身时长 = (1/2 + 2) × 1.0 = 2.5 回合
		check(Math.abs(target.buff(Roots.class).cooldown() - 2.5f) < 0.0001f,
				"1 级骨手的定身时长应为 2.5 回合：" + target.buff(Roots.class).cooldown());

		//充能为 0 时不能再指向（charge 不会被扣成负数）
		hand.pointAt(target);
		hand.pointAt(target);
		check(hand.charge() == 0, "充能不应被扣成负数：" + hand.charge());
	}

	private static void testRechargeAndSave() {
		Hero hero = new Hero();
		Dungeon.hero = hero;
		HandOfTheElder hand = new HandOfTheElder();
		hero.belongings.backpack.items.add(hand);
		check(hand.doEquip(hero), "古老者之手无法装备");
		hand.charge = 0;
		hand.partialCharge = 0f;

		HandOfTheElder.Recharge recharge = hand.new Recharge();
		check(recharge.attachTo(hero), "古老者之手充能状态无法附加");
		for (int i = 0; i < 100; i++) recharge.act();
		check(hand.charge() >= 1, "骨手在背包中没有恢复充能：" + hand.charge());

		RingOfFuror furor = new RingOfFuror();
		check(hand.wearRing(furor), "古老者之手无法镶嵌戒指");
		Bundle bundle = new Bundle();
		hand.storeInBundle(bundle);
		HandOfTheElder restored = new HandOfTheElder();
		restored.restoreFromBundle(bundle);
		check(restored.rings().size() == 1 && restored.rings().get(0) == RingOfFuror.class,
				"已镶嵌的戒指没有随存档恢复");
	}

	private static void testPoolAndMessages() throws Exception {
		boolean inPool = false;
		for (Class<?> type : Generator.Category.ARTIFACT.classes) {
			if (type == HandOfTheElder.class) inPool = true;
		}
		check(inPool, "古老者之手没有进入普通神器池");
		check(Generator.Category.ARTIFACT.classes.length == Generator.Category.ARTIFACT.defaultProbs.length,
				"神器池与权重数组长度不一致");

		for (String file : new String[]{"messages/items/zh/items.properties", "messages/items/en/items.properties"}) {
			String text = read(file);
			check(text.contains("items.equipment.artifacts.handoftheelder.desc="), file + "缺少古老者之手描述");
			check(text.contains("items.equipment.artifacts.handoftheelder.ac_point="), file + "缺少指向动作名");
			check(text.contains("items.equipment.artifacts.handoftheelder.ac_wear="), file + "缺少镶嵌动作名");
		}
	}

	private static String read(String path) throws Exception {
		return new String(Files.readAllBytes(Path.of(path)), StandardCharsets.UTF_8);
	}

	private static final class TestMob extends Mob {
		TestMob(int health) {
			HP = HT = health;
		}

		@Override public int damageRoll() { return 1; }
		@Override public int attackSkill(Char target) { return 1; }
		@Override public int defenseSkill(Char enemy) { return 0; }
		@Override public int drRoll() { return 0; }
		@Override public void die(Object cause) { }
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsHandOfTheElderTest() { }
}
