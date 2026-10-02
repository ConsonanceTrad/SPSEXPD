package pd.items;

import pd.atlas.items.SpecificPlaceHolderDict;

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
import pd.actors.buffs.AdrenalineSurge;
import pd.actors.buffs.ArmorBreak;
import pd.actors.buffs.AttackDown;
import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Bless;
import pd.actors.buffs.Buff;
import pd.actors.buffs.DefenceUp;
import pd.actors.buffs.FireImbue;
import pd.actors.buffs.FullMoonStrength;
import pd.actors.buffs.Light;
import pd.actors.buffs.LingBless;
import pd.actors.buffs.MoonFury;
import pd.actors.buffs.ToxicImbue;
import pd.actors.hero.Hero;
import pd.actors.mobs.npcs.TownNpc;
import pd.items.consum.food.fruit.FullMoonberry;
import pd.items.consum.medicine.LingPotion;
import pd.items.equipment.rings.Ring;
import pd.items.equipment.weapon.melee.MeleeWeapon;
import render.noosa.Game;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Arrays;

import javax.imageio.ImageIO;

public final class SpsLegacyConsumableBuffsTest {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Ring.initGems();
		Random.pushGenerator(0x5350534D4F4F4E4CL);
		try {
			testLegacyLevelBuffSaves();
			testFullMoonStrength();
			testFullMoonberry();
			testLingBlessAndPotion();
			testSourcesResourcesAndIcon();
			System.out.println("SPS独有消耗品状态通过：旧攻防等级状态时长、满月之力、澪祷之愿、来源、存档、原文与原始图标均正常。");
		} finally {
			Random.popGenerator();
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			app.exit();
		}
	}

	private static void testFullMoonStrength() {
		Hero hero = hero();
		hero.belongings.weapon = new FixedWeapon();
		FullMoonStrength strength = Buff.affect(hero, FullMoonStrength.class).setHits(3);
		Buff.affect(hero, MoonFury.class);
		for (int hit = 0; hit < 4; hit++) {
			check(hero.damageRoll() == 15, "满月之力第" + (hit + 1) + "次攻击没有保持三倍月怒");
		}
		check(hero.buff(FullMoonStrength.class) == null && hero.buff(MoonFury.class) == null,
				"满月之力次数耗尽后没有移除状态");
		check(hero.damageRoll() == 5, "满月之力耗尽后仍在提高伤害");
		check(FullMoonStrength.hitsFor(false, 12) == 5
				&& FullMoonStrength.hitsFor(true, 12) == 11, "满月之力昼夜或深度次数公式错误");

		Bundle bundle = new Bundle();
		strength.setHits(7).storeInBundle(bundle);
		FullMoonStrength restored = new FullMoonStrength();
		restored.restoreFromBundle(bundle);
		check(restored.hits() == 7, "满月之力剩余攻击次数没有随存档恢复");
	}

	private static void testLegacyLevelBuffSaves() {
		checkLegacyLevelBuff(new ArmorBreak());
		checkLegacyLevelBuff(new AttackDown());
		checkLegacyLevelBuff(new AttackUp());
		checkLegacyLevelBuff(new DefenceUp());
	}

	private static void checkLegacyLevelBuff(pd.actors.buffs.FlavourBuff buff) {
		Bundle legacy = new Bundle();
		legacy.put("level", 25);
		legacy.put("left", 14f);
		buff.restoreFromBundle(legacy);
		int level = buff instanceof ArmorBreak ? ((ArmorBreak)buff).level()
				: buff instanceof AttackDown ? ((AttackDown)buff).level()
				: buff instanceof AttackUp ? ((AttackUp)buff).level() : ((DefenceUp)buff).level();
		check(level == 25 && buff.cooldown() >= 14f,
				buff.getClass().getSimpleName() + "没有读取旧版level/left字段");
		Bundle saved = new Bundle();
		buff.storeInBundle(saved);
		check(saved.getInt("level") == 25 && saved.getFloat("left") >= 14f,
				buff.getClass().getSimpleName() + "没有双写旧版状态字段");
	}

	private static void testFullMoonberry() {
		Hero hero = hero();
		new TestFullMoonberry().apply(hero);
		check(hero.buff(MoonFury.class) != null && hero.buff(FullMoonStrength.class) != null
				&& hero.buff(Light.class) != null, "满月浆果没有给予月怒、满月之力和照明");
		check(hero.buff(Bless.class) == null && hero.buff(AdrenalineSurge.class) == null,
				"满月浆果仍在使用替代的破碎增益");
		check(new FullMoonberry().value() == 5, "满月浆果售价不是旧版5金币");
		check(Arrays.asList(Generator.Category.BERRY.classes).contains(FullMoonberry.class),
				"满月浆果没有保留在浆果牌组");
	}

	private static void testLingBlessAndPotion() {
		Hero hero = hero();
		int normalDefense = hero.defenseSkill(null);
		float normalSpeed = hero.speed();
		new TestLingPotion().apply(hero);
		LingBless blessing = hero.buff(LingBless.class);
		check(blessing != null && blessing.cooldown() >= 200f, "澪祷星瓶没有给予200回合澪祷之愿");
		check(hero.defenseSkill(null) == Math.round(normalDefense * 1.2f), "澪祷之愿闪避提升不是20%");
		check(close(hero.speed(), normalSpeed + 0.2f), "澪祷之愿速度没有增加0.2");
		check(hero.buff(Bless.class) == null && hero.buff(FireImbue.class) == null
				&& hero.buff(ToxicImbue.class) == null, "澪祷星瓶仍在给予替代的火毒亲和");
		LingPotion potion = new LingPotion();
		check(potion.value() == 50 && potion.image == SpecificPlaceHolderDict.SOMETHING_0,
				"澪祷星瓶价格或原始图标槽错误");
	}

	private static void testSourcesResourcesAndIcon() throws Exception {
		check(new TownNpc().configure(TownNpc.Spec.FLY_LING).SupercreateLoot() instanceof LingPotion,
				"澪的特殊奖励不是澪祷星瓶");
		String zhItems = Files.readString(Paths.get("messages/items/zh/items.properties"), StandardCharsets.UTF_8);
		String zhActors = Files.readString(Paths.get("messages/actors/zh/actors.properties"), StandardCharsets.UTF_8);
		String enItems = Files.readString(Paths.get("messages/items/en/items.properties"), StandardCharsets.UTF_8);
		check(zhItems.contains("items.medicine.lingpotion.name=澪祷星瓶")
				&& zhItems.contains("一瓶来自澪的圣水，能极大提升使用者的能力")
				&& zhItems.contains("items.food.fruit.fullmoonberry.name=满月浆果"), "消耗品中文原文缺失");
		check(zhActors.contains("actors.buffs.fullmoonstrength.name=满月之力")
				&& zhActors.contains("actors.buffs.lingbless.name=澪祷之愿")
				&& !zhItems.contains("�") && !zhActors.contains("�"), "独有状态中文乱码或缺失");
		check(enItems.contains("It was blessed by Selemene."), "满月浆果英文原文缺失");

		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		check("363CA1AEF7BF922A25167EB80B19B527CDFAD153E34901B093DE76B7C35C6954".equals(
				hash(sheet, SpecificPlaceHolderDict.SOMETHING_0)), "澪祷星瓶不是0.9.8原始图标");
	}

	private static Hero hero() {
		Actor.clear();
		Dungeon.quickslot = new QuickSlot();
		Hero hero = new Hero();
		Dungeon.hero = hero;
		return hero;
	}

	private static String hash(BufferedImage sheet, IconEntry itemIndex) throws Exception {
		int left = (itemIndex % 16) * 16;
		int top = (itemIndex / 16) * 16;
		ByteBuffer pixels = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
		for (int y = top; y < top + 16; y++) {
			for (int x = left; x < left + 16; x++) pixels.putInt(sheet.getRGB(x, y));
		}
		byte[] digest = MessageDigest.getInstance("SHA-256").digest(pixels.array());
		StringBuilder out = new StringBuilder(64);
		for (byte value : digest) out.append(String.format("%02X", value & 0xFF));
		return out.toString();
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

	private static final class TestFullMoonberry extends FullMoonberry {
		void apply(Hero hero) { onEat(hero); }
	}

	private static final class TestLingPotion extends LingPotion {
		void apply(Hero hero) { onUse(hero); }
	}

	private SpsLegacyConsumableBuffsTest() { }
}
