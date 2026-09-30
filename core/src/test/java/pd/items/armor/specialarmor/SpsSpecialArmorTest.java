package pd.items.armor.specialarmor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.*;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.actors.mobs.Mob;
import pd.items.armor.normalarmor.NormalArmor;
import pd.items.weapon.guns.GunA;
import pd.sprites.ItemSpriteSheet;
import render.noosa.Game;
import render.utils.Bundle;
import render.utils.Random;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;

import javax.imageio.ImageIO;

/** Headless regression checks for the eight SPS hero armors produced by ArmorKit. */
public final class SpsSpecialArmorTest {
	private static final Class<?>[] CLASSES = {WarriorArmor.class, MageArmor.class, RogueArmor.class,
			HuntressArmor.class, PerformerArmor.class, SoldierArmor.class, FollowerArmor.class, AsceticArmor.class};
	private static final HeroClass[] HERO_CLASSES = {HeroClass.WARRIOR, HeroClass.MAGE, HeroClass.ROGUE,
			HeroClass.HUNTRESS, HeroClass.PERFORMER, HeroClass.SOLDIER, HeroClass.FOLLOWER, HeroClass.ASCETIC};
	private static final int[] TIERS = {7, 1, 1, 2, 2, 5, 4, 3};
	private static final float[] DEX = {1f, 3f, 5f, 2.4f, 4f, 1f, 3.5f, 3.5f};
	private static final float[] STE = {1f, 7f, 13f, 6f, 12f, 1f, 10f, 11f};
	private static final int[] ENG = {2, 4, 2, 4, 3, 2, 5, 4};
	private static final int[] BASE_MIN = {20, 0, 0, 0, 0, 20, 0, 0};
	private static final int[] BASE_MAX = {40, 4, 2, 12, 10, 40, 20, 15};
	private static final int[] MIN_GROWTH = {3, 1, 1, 1, 1, 0, 1, 1};
	private static final int[] MAX_GROWTH = {5, 4, 3, 3, 3, 3, 3, 3};
	private static final int[] STR_OFFSET = {0, 0, -1, 0, -1, 1, -1, -1};
	private static final int[] IMAGES = {ItemSpriteSheet.SPS_ARMOR_WARRIOR, ItemSpriteSheet.SPS_ARMOR_MAGE,
			ItemSpriteSheet.SPS_ARMOR_ROGUE, ItemSpriteSheet.SPS_ARMOR_HUNTRESS,
			ItemSpriteSheet.SPS_ARMOR_PERFORMER, ItemSpriteSheet.SPS_ARMOR_SOLDIER,
			ItemSpriteSheet.SPS_ARMOR_FOLLOWER, ItemSpriteSheet.SPS_ARMOR_ASCETIC};
	private static final String[] ICON_HASHES = {
			"0A5E2A7FB7223193F3BFA54DC89B532B75D0E8EB564438F925A02831206D2D21",
			"F57A0C546372F9C12CA63C7CEE5341B4F45E7F289E8D76ABFB588E843EAF87CB",
			"68CD2F8FD24A12AB4F3F130A468FB283845322D4EA8F5F18D204DE50EE39449B",
			"E290C7453FC776DCC4C74E3E71A0A0CF0D9E3206E2598C566D05826C68530485",
			"92464D280209521D9B6C196B012B60039D203F435A0CA8B8A206AAEA26231D6A",
			"DA89372EA5833ACCB3C5CC3BF9245AB7C99472E3B4E254AD3E66FFEDD011172A",
			"69B381F28DFB957F95E7B99BFE438697E6241F09A67C72A740FAF0CA17917431",
			"A8C9C79BC885A9C9022D883E1CC2F09968C4C3DCA4D59F4E1366E973E1044BD6"
	};

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Random.pushGenerator(0x53505341524D4F52L);
		try {
			testDefinitionsAndArmorKitMapping();
			testPassiveEffects();
			testSaveRestore();
			testLegacyIcons();
			System.out.println("SPS八职业特殊护甲测试通过：护甲包映射、数值、受击被动、存档和原始图标均正常。");
		} finally {
			Random.popGenerator();
		}
	}

	private static void testDefinitionsAndArmorKitMapping() throws Exception {
		for (int i = 0; i < CLASSES.length; i++) {
			Hero hero = hero();
			hero.heroClass = HERO_CLASSES[i];
			NormalArmor mapped = NormalArmor.upgrade(hero);
			check(mapped.getClass() == CLASSES[i], HERO_CLASSES[i] + "的ArmorKit映射错误");
			NormalArmor armor = (NormalArmor)CLASSES[i].getDeclaredConstructor().newInstance();
			check(armor.tier == TIERS[i] && close(armor.DEX, DEX[i]) && close(armor.STE, STE[i])
					&& armor.ENG == ENG[i], armor.getClass().getSimpleName() + "阶级或SPS属性错误");
			check(armor.DRMin(0) == BASE_MIN[i] && armor.DRMax(0) == BASE_MAX[i]
					&& armor.DRMin(4) == BASE_MIN[i] + 4 * MIN_GROWTH[i]
					&& armor.DRMax(4) == BASE_MAX[i] + 4 * MAX_GROWTH[i],
					armor.getClass().getSimpleName() + "防御或成长错误");
			int strength = 8 + TIERS[i] * 2 + STR_OFFSET[i];
			check(armor.STRReq(0) == strength && armor.STRReq(10) == strength,
					armor.getClass().getSimpleName() + "力量需求错误");
			check(armor.image == IMAGES[i], armor.getClass().getSimpleName() + "图标槽错误");
		}
	}

	private static void testPassiveEffects() {
		Hero hero = hero();
		TestMob attacker = mob(100_000);
		for (int i = 0; i < 1_000 && hero.buff(MagicArmor.class) == null; i++)
			new WarriorArmor().proc(attacker, hero, 40);
		check(hero.buff(MagicArmor.class) != null && hero.buff(MagicArmor.class).level() == 40,
				"战士重甲没有生成法术护甲");

		boolean negated = false;
		for (int i = 0; i < 1_000 && !negated; i++) negated = new MageArmor().proc(attacker, hero, 40) == 0;
		check(negated, "法师长袍没有免疫伤害");

		Dungeon.gold = 0;
		for (int i = 0; i < 1_000 && Dungeon.gold == 0; i++) new RogueArmor().proc(attacker, hero, 40);
		check(Dungeon.gold == 40, "盗贼风衣没有按伤害偷取金币");

		int startHP = attacker.HP;
		for (int i = 0; i < 1_000 && attacker.HP == startHP; i++) new HuntressArmor().proc(attacker, hero, 40);
		check(attacker.HP == startHP - 40, "猎手披风没有反弹完整伤害");

		for (int i = 0; i < 1_000 && attacker.buff(Charm.class) == null; i++)
			new PerformerArmor().proc(attacker, hero, 40);
		check(attacker.buff(Charm.class) != null, "演员夹克没有魅惑攻击者");

		GunA gun = new GunA();
		hero.belongings.backpack.items.add(gun);
		for (int i = 0; i < 1_000 && hero.buff(TargetShoot.class) == null; i++)
			new SoldierArmor().proc(attacker, hero, 40);
		check(hero.buff(TargetShoot.class) != null && gun.charge() == 1,
				"星兵背心没有补充枪弹或提供瞄准状态");

		hero.HP = 1;
		for (int i = 0; i < 1_000 && hero.HP == 1; i++) new FollowerArmor().proc(attacker, hero, 40);
		check(hero.HP == 11, "信徒外套没有恢复四分之一所受伤害");

		for (int i = 0; i < 1_000 && hero.buff(HasteBuff.class) == null; i++)
			new AsceticArmor().proc(attacker, hero, 40);
		check(hero.buff(HasteBuff.class) != null, "修士躯壳没有提供超频加速");
	}

	private static void testSaveRestore() {
		WarriorArmor source = new WarriorArmor();
		source.upgrade(3);
		Bundle bundle = new Bundle();
		source.storeInBundle(bundle);
		WarriorArmor restored = new WarriorArmor();
		restored.restoreFromBundle(bundle);
		check(restored.level() == 3 && restored.DRMin() == 29 && restored.DRMax() == 55,
				"特殊护甲升级数值没有随存档恢复");
	}

	private static void testLegacyIcons() throws Exception {
		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		ByteBuffer pixels = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
		for (int slot = 0; slot < ICON_HASHES.length; slot++) {
			pixels.clear();
			int left = 32 + slot * 16;
			for (int y = 704; y < 720; y++) {
				for (int x = left; x < left + 16; x++) pixels.putInt(sheet.getRGB(x, y));
			}
			check(ICON_HASHES[slot].equals(toHex(digest.digest(pixels.array()))),
					"第" + (slot + 1) + "件特殊护甲图标与旧版像素不一致");
		}
	}

	private static Hero hero() {
		Hero hero = new Hero();
		hero.HP = hero.HT = 100;
		hero.STR = 100;
		Dungeon.hero = hero;
		return hero;
	}
	private static TestMob mob(int health) { TestMob mob = new TestMob(); mob.HP = mob.HT = health; return mob; }
	private static boolean close(float a, float b) { return Math.abs(a - b) < 0.0001f; }
	private static String toHex(byte[] bytes) {
		StringBuilder result = new StringBuilder(bytes.length * 2);
		for (byte value : bytes) result.append(String.format("%02X", value & 0xFF));
		return result.toString();
	}
	private static final class TestMob extends Mob {
		@Override public int damageRoll() { return 1; }
		@Override public int attackSkill(Char target) { return 1; }
		@Override public int drRoll() { return 0; }
	}
	private static void check(boolean condition, String message) { if (!condition) throw new AssertionError(message); }
	private SpsSpecialArmorTest() { }
}
