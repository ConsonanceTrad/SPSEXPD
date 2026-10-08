package pd.items.equipment.weapon.melee.normalweapon;

import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;
import pd.atlas.items.SpecificPlaceHolderDict;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.ArmorBreak;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.items.Generator;
import pd.items.equipment.weapon.melee.MeleeWeapon;
import render.noosa.Game;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import pd.atlas.IconEntry;

/** Headless regression checks for SPS-PD 0.9.8's 20 ordinary melee weapons. */
public final class SpsLegacyMeleeTest {

	private static final Class<?>[] CLASSES = {
			Dagger.class, Knuckles.class, ShortSword.class, MageBook.class,
			Handaxe.class, Spear.class, Dualknive.class, FightGloves.class,
			Nunchakus.class, Scimitar.class, Whip.class, Rapier.class,
			AssassinsBlade.class, BattleAxe.class, Glaive.class, Club.class,
			Gsword.class, Halberd.class, WarHammer.class, Lance.class
	};
	private static final int[] TIERS = {1,1,1,1, 2,2,2,2, 3,3,3,3, 4,4,4,4, 5,5,5,5};
	private static final int[] BASE_MIN = {1,1,1,1, 11,14,11,11, 18,23,24,18, 26,36,42,28, 50,62,41,35};
	private static final int[] BASE_MAX = {10,10,10,10, 22,30,17,17, 27,35,35,25, 34,49,60,40, 64,82,56,44};
	private static final int[] LEVEL4_MIN = {9,9,17,9, 23,22,19,19, 30,31,32,22, 42,40,50,44, 66,66,49,43};
	private static final int[] LEVEL4_MAX = {18,18,26,22, 42,54,29,29, 39,59,51,49, 50,85,96,56, 80,114,80,68};
	private static final IconEntry[] images = {
			SpecificPlaceHolderDict.SOMETHING_0, SpecificPlaceHolderDict.SOMETHING_0,
			SpecificPlaceHolderDict.SOMETHING_0, SpecificPlaceHolderDict.SOMETHING_0,
			SpecificPlaceHolderDict.SOMETHING_0, EquipmentEquipWeaponBasicWeaponDict.SPS_WEP_SPEAR_0,
			SpecificPlaceHolderDict.SOMETHING_0, SpecificPlaceHolderDict.SOMETHING_0,
			SpecificPlaceHolderDict.SOMETHING_0, SpecificPlaceHolderDict.SOMETHING_0,
			SpecificPlaceHolderDict.SOMETHING_0, SpecificPlaceHolderDict.SOMETHING_0,
			SpecificPlaceHolderDict.SOMETHING_0, SpecificPlaceHolderDict.SOMETHING_0,
			SpecificPlaceHolderDict.SOMETHING_0, EquipmentEquipWeaponBasicWeaponDict.SPS_WEP_CLUB_0,
			SpecificPlaceHolderDict.SOMETHING_0, SpecificPlaceHolderDict.SOMETHING_0,
			SpecificPlaceHolderDict.SOMETHING_0, SpecificPlaceHolderDict.SOMETHING_0
	};
	private static final String[] ICON_HASHES = {
			"26B3EE06F8D473CFC5DFF1359A40683740E900BCC6357D32DA7FFDE5F1A736B9",
			"571F523DC905ACF7765DA3EC254A68CBBC28CB910E6ABD4EB823891B18D76B3E",
			"F740B297A0B0936A82F534B8BA77FEE1133532E064DBAB06273FF203C51E5E4D",
			"97AA485819A6F1AF6F49294E3BAC09087370884AFB738B164E5EC54EA8D213B6",
			"8D9191D9CF5D54398510B6E18301AEEA818B8F7100FC89E5A89A36B28A4CE522",
			"25770F7D6D8734CB56D261F312FD739A05E095E280E12D0BE94F8D8A6423A997",
			"19127B9BA52056C5B4966D6D608CA920082F5CCCAE6F98B9267FCD019E6A483D",
			"1166820F6643B44406EE9B2E4E37F809DC6C57BD7767A97CE032750D17A2779A",
			"1F76C6C5B0DCD0DC2ED6356E283EBAC767AD9EA9737BFE7FFC12DF5094D1934E",
			"13BF00CD6EA2CDB457908C78B5896D5A1A306F06BC1A73DB5CFA7D800BFA5ACF",
			"59D35AF84DEEAF7C257EAB52D5E6932A6814B4760F10A52C0C81458CC539DEED",
			"1AAFDE115819CFD508C255A4A1BB1A31DA615F5B2D6E669F738B511658D0D22C",
			"D9A1E013925629BB8E22713177A18AE2D8BAE94DC272BAF1EB2A37D9496C9D94",
			"7757E5573B6D6AE56690F44CABE504CAF381E2FA9C2655CE2985BDC35E6AB10D",
			"00E0B81EAE77EADE012D5A733899441FCD05809D254D1D9518383717B860AABF",
			"2ECB97EAC6DCC287F8273F68380581E561D1B5F1BB66A96B77EA7CB37FA15FC5",
			"5DC2E779F70472541195B9A9C504EC84D108A47E8641B88722A28873A5881FBC",
			"B3CCC95205735CEDBCFD403C60D94B832E828F2FA793A2DB9CD07472683A30F8",
			"53A6884B830A72BFBB736C17644CB0C167A50CF283E47C46D4E68F38CC699D45",
			"04F896C0805748CAA53B91F4B04DF6B182B2E8D61DCF125EEE52A98BBF6D005E"
	};

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Random.pushGenerator(0x5350534D454C4545L);
		try {
			testDefinitionsAndDeck();
			testStatProgression();
			testCombatAndSafeBounds();
			testSaveRestore();
			testLegacyIcons();
			System.out.println("SPS旧版20件普通近战武器测试通过：牌组、数值、成长、战斗效果、存档和原始图标均正常。");
		} finally {
			Random.popGenerator();
		}
	}

	private static void testDefinitionsAndDeck() throws Exception {
		//SPSEXPD: 旧版 20 件之后追加三相之力与血源风猎魂武器两件（普通近战池共 43 项），前 20 项顺序不变
		check(Generator.Category.MELEEWEAPON.classes.length == 43, "旧版近战武器牌组数量错误");
		Set<Class<?>> generated = new HashSet<>();
		for (int i = 0; i < CLASSES.length; i++) {
			check(Generator.Category.MELEEWEAPON.classes[i] == CLASSES[i], "普通近战武器生成顺序错误：" + i);
			check(Generator.Category.MELEEWEAPON.probs[i] == 1f, "普通近战武器生成权重错误：" + i);
			NormalMeleeWeapon weapon = (NormalMeleeWeapon)CLASSES[i].getDeclaredConstructor().newInstance();
			check(weapon.tier == TIERS[i], weapon.getClass().getSimpleName() + "阶级错误");
			check(weapon.min(0) == BASE_MIN[i] && weapon.max(0) == BASE_MAX[i], weapon.getClass().getSimpleName() + "基础伤害错误");
			check(weapon.STRReq(0) == 8 + 2 * TIERS[i], weapon.getClass().getSimpleName() + "基础力量错误");
			check(weapon.image == images[i], weapon.getClass().getSimpleName() + "图标槽错误");
		}
		for (int i = 0; i < 10_000; i++) generated.add(Generator.randomWeaponForStrength(14).getClass());
		for (Class<?> weaponClass : CLASSES) {
			check(generated.contains(weaponClass), "按力量抽取无法生成普通近战武器：" + weaponClass.getSimpleName());
		}
	}

	private static void testStatProgression() throws Exception {
		for (int i = 0; i < CLASSES.length; i++) {
			NormalMeleeWeapon weapon = (NormalMeleeWeapon)CLASSES[i].getDeclaredConstructor().newInstance();
			check(weapon.min(4) == LEVEL4_MIN[i] && weapon.max(4) == LEVEL4_MAX[i],
					weapon.getClass().getSimpleName() + "四级伤害成长错误");
		}
		check(close(new Dagger().legacyAccuracy(4), 1.8f), "匕首命中成长错误");
		check(close(new Knuckles().legacyDelay(20), .3f) && new Knuckles().legacyReach(20) == 2, "指虎攻速或距离成长错误");
		check(new MageBook().STRReq(10) == 1 && new MageBook().legacyReach(10) == 3, "魔典力量或距离成长错误");
		check(new Handaxe().STRReq(10) == 10, "手斧力量成长错误");
		check(new Spear().legacyReach(20) == 3, "长矛距离成长错误");
		check(new FightGloves().legacyReach(20) == 2, "战斗拳套距离成长错误");
	}

	private static void testCombatAndSafeBounds() {
		Hero hero = new Hero();
		hero.HP = hero.HT = 100;
		Dungeon.hero = hero;
		TestMob attacker = mob(100_000);
		TestMob defender = mob(100_000);
		int hp = defender.HP;
		new Dagger().proc(attacker, defender, 0);
		check(defender.HP < hp, "匕首没有追加伤害");
		for (int i = 0; i < 1_000 && defender.buff(ArmorBreak.class) == null; i++) new Scimitar().proc(attacker, defender, 0);
		check(defender.buff(ArmorBreak.class) != null && defender.buff(ArmorBreak.class).level() == 30, "弯刀没有施加破甲");
		NormalMeleeWeapon[] weapons = {new ShortSword(), new Handaxe(), new BattleAxe(), new Gsword(),
				new Dualknive(), new AssassinsBlade(), new Lance(), new Rapier()};
		for (NormalMeleeWeapon weapon : weapons) weapon.proc(attacker, defender, 0);
	}

	private static void testSaveRestore() {
		MageBook source = new MageBook();
		source.level(10);
		Bundle bundle = new Bundle();
		source.storeInBundle(bundle);
		MageBook restored = new MageBook();
		restored.restoreFromBundle(bundle);
		check(restored.level() == 10 && restored.min() == source.min() && restored.max() == source.max(), "魔典升级伤害没有随存档恢复");
		check(restored.STRReq() == 1 && restored.legacyReach(restored.level()) == 3, "魔典升级属性没有随存档恢复");
	}

	private static void testLegacyIcons() throws Exception {
		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		check(sheet != null && sheet.getWidth() == 256 && sheet.getHeight() == 992, "物品图集不是256x992像素");
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		ByteBuffer pixels = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
		for (int slot = 0; slot < ICON_HASHES.length; slot++) {
			pixels.clear();
			int left = (slot % 16) * 16;
			int top = 720 + (slot / 16) * 16;
			for (int y = top; y < top + 16; y++) {
				for (int x = left; x < left + 16; x++) pixels.putInt(sheet.getRGB(x, y));
			}
			check(ICON_HASHES[slot].equals(toHex(digest.digest(pixels.array()))), "第" + (slot + 1) + "件普通近战武器图标错误");
		}
	}

	private static TestMob mob(int health) { TestMob mob = new TestMob(); mob.HP = mob.HT = health; return mob; }
	private static boolean close(float a, float b) { return Math.abs(a - b) < .0001f; }
	private static String toHex(byte[] bytes) {
		StringBuilder result = new StringBuilder(bytes.length * 2);
		for (byte value : bytes) result.append(String.format("%02X", value & 0xFF));
		return result.toString();
	}
	private static final class TestMob extends Mob {
		@Override public int damageRoll() { return 20; }
		@Override public int attackSkill(Char target) { return 1; }
		@Override public int drRoll() { return 0; }
	}
	private static void check(boolean condition, String message) { if (!condition) throw new AssertionError(message); }
	private SpsLegacyMeleeTest() { }
}
