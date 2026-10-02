package pd.items.equipment.weapon.ranges;

import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;
import pd.atlas.items.SpecificPlaceHolderDict;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.items.Generator;
import pd.items.Item;
import pd.items.equipment.weapon.guns.GunA;
import pd.items.equipment.weapon.guns.GunB;
import pd.items.equipment.weapon.guns.GunC;
import pd.items.equipment.weapon.guns.GunD;
import pd.items.equipment.weapon.guns.GunE;
import pd.items.equipment.weapon.melee.Dagger;
import pd.levels.rooms.special.SpsShopRoom;
import render.noosa.Game;
import render.utils.math.Random;

import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.util.HashSet;
import javax.imageio.ImageIO;
import pd.atlas.IconEntry;

/** Headless checks for all fifteen SPS bow variants and their gameplay entry points. */
public final class SpsRangeWeaponTest {

	private static final Class<?>[][] BOW_TYPES = {
			{WoodenBowN.class, WoodenBowS.class, WoodenBowR.class},
			{StoneBowN.class, StoneBowS.class, StoneBowR.class},
			{MetalBowN.class, MetalBowS.class, MetalBowR.class},
			{AlloyBowN.class, AlloyBowS.class, AlloyBowR.class},
			{PVCBowN.class, PVCBowS.class, PVCBowR.class}
	};
	private static final IconEntry[] BOW_IMAGES = {
			EquipmentEquipWeaponBasicWeaponDict.WOODEN_BOW, SpecificPlaceHolderDict.SOMETHING_0, SpecificPlaceHolderDict.SOMETHING_0,
			SpecificPlaceHolderDict.SOMETHING_0, SpecificPlaceHolderDict.SOMETHING_0
	};
	private static final String[] ICON_HASHES = {
			"C4EFCAA0D0CB2F58F29AA5E38D3DACAA51F1BAD8F3C33F490AE5D90EB07D03EA",
			"0894D2555E45A57A297CD02CA1CB486B28D744F076E78156E46377897CB66CC1",
			"F89DDDDD637B32E4135DFBA6577402AD99C04670F5738D9C8999B395FA21DE6E",
			"0081CC0CBAD74D35C7BECD217ADECD589134C62A7524E9411BAB46B5961D1EA6",
			"DB5143D5FC226D80456D5B167EDBC463EDDCCD925C11094B38F8E73C1BB34DA3",
			"ED21916647CDEB91C5ABA59A6EE4003CCD28A9746F20AA275671A7D017AF66D0"
	};

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Random.pushGenerator(0x535053424F57534CL);
		try {
			testDefinitions();
			testShootingRules();
			testGeneratorDeck();
			testChapterShops();
			testLegacyIcons();
			System.out.println("SPS弓械测试通过：15把弓、三种型号成长、无限箭矢、特工射击、20件远程牌组、五章商店和6个旧版图标均正常。");
		} finally {
			Random.popGenerator();
		}
	}

	private static void testDefinitions() throws Exception {
		for (int tierIndex = 0; tierIndex < BOW_TYPES.length; tierIndex++) {
			int tier = tierIndex + 1;
			for (int variant = 0; variant < 3; variant++) {
				RangeWeapon bow = (RangeWeapon)BOW_TYPES[tierIndex][variant].getDeclaredConstructor().newInstance();
				float delay = variant == 0 ? 1f : variant == 1 ? 0.8f : 1.25f;
				int baseMin = (int)((tier + 3) * delay);
				int baseMax = (int)((tier * tier - tier + 8) * delay);
				int maxGrowth = 1 + tier / 2 + (variant == 2 ? tier + 1 : 0);
				check(bow.min(0) == baseMin && bow.max(0) == baseMax,
						bow.getClass().getSimpleName() + "基础伤害错误");
				check(bow.min(4) == baseMin + (variant == 1 ? 0 : 4)
						&& bow.max(4) == baseMax + maxGrowth * 4,
						bow.getClass().getSimpleName() + "强化成长错误");
				int strengthShift = variant == 0 ? 0 : variant == 1 ? -1 : 1;
				check(bow.STRReq(0) == 8 + tier * 2 + strengthShift
						&& bow.STRReq(4) == bow.STRReq(0),
						bow.getClass().getSimpleName() + "固定力量需求错误");
				check(Math.abs(bow.DLY - delay) < 0.00001f && bow.image == BOW_IMAGES[tierIndex],
						bow.getClass().getSimpleName() + "射速或图标错误");
				check(bow.isUpgradable() && bow.value() == 100, bow.getClass().getSimpleName() + "强化或价值错误");
			}
		}
	}

	private static void testShootingRules() {
		Hero hero = new Hero();
		Dungeon.hero = hero;
		WoodenBowN normal = new WoodenBowN();
		check(!normal.canShoot(hero), "普通职业未装备弓时仍可射击");
		hero.subClass = HeroSubClass.AGENT;
		check(normal.canShoot(hero), "特工未装备弓时不能射击");
		check(normal.new NormalArrow().image == SpecificPlaceHolderDict.SOMETHING_0,
				"临时箭矢没有使用旧版图标");
		check(normal.new NormalArrow().spawnedForEffect, "无限箭矢命中后会掉落实体物品");
		check(Math.abs(new WoodenBowS().new NormalArrow().delayFactor(hero) - 0.8f) < 0.00001f,
				"轻型弓箭矢延迟错误");
		check(Math.abs(new WoodenBowR().new NormalArrow().delayFactor(hero) - 1.25f) < 0.00001f,
				"重型弓箭矢延迟错误");
		hero.STR = 9;
		check(normal.damageRoll(hero) == 0, "力量不足时近战挥弓仍造成伤害");
		check(normal.rangedDamageRoll(hero) > 0, "旧版力量不足时射箭错误变成零伤害");
	}

	private static void testGeneratorDeck() {
		Class<?>[] expected = {
				WoodenBowN.class, WoodenBowS.class, WoodenBowR.class, GunA.class,
				StoneBowN.class, StoneBowS.class, StoneBowR.class, GunB.class,
				MetalBowN.class, MetalBowS.class, MetalBowR.class, GunC.class,
				AlloyBowN.class, AlloyBowS.class, AlloyBowR.class, GunD.class,
				PVCBowN.class, PVCBowS.class, PVCBowR.class, GunE.class
		};
		check(Generator.Category.RANGED.classes.length == expected.length
				&& Generator.Category.RANGED.probs.length == expected.length,
				"远程武器牌组不是20件");
		for (int i = 0; i < expected.length; i++) {
			check(Generator.Category.RANGED.classes[i] == expected[i]
					&& Generator.Category.RANGED.probs[i] == 1f, "远程武器牌组顺序或权重错误");
		}
		check(Generator.Category.order(new GunA()) / 100 == Generator.Category.RANGED.ordinal(),
				"枪械没有归入SPS远程牌组");
		check(Generator.Category.order(new WoodenBowN()) / 100 == Generator.Category.RANGED.ordinal(),
				"弓没有归入SPS远程牌组");
		check(Generator.Category.order(new Dagger()) / 100 != Generator.Category.RANGED.ordinal(),
				"破碎近战武器错误归入SPS远程牌组");
	}

	private static void testChapterShops() throws Exception {
		Method method = SpsShopRoom.class.getDeclaredMethod("chapterShootWeapon");
		method.setAccessible(true);
		int[] depths = {0, 6, 11, 16, 21};   //SPS: 0 层为第一章特殊层（原 1 层）
		Class<?>[] bows = {WoodenBowN.class, StoneBowN.class, MetalBowN.class, AlloyBowN.class, PVCBowN.class};
		Class<?>[] guns = {GunA.class, GunB.class, GunC.class, GunD.class, GunE.class};
		for (int i = 0; i < depths.length; i++) {
			Dungeon.depth = depths[i];
			HashSet<Class<?>> seen = new HashSet<>();
			for (int attempt = 0; attempt < 64; attempt++) {
				Item result = (Item)method.invoke(null);
				check(result.getClass() == bows[i] || result.getClass() == guns[i],
						"深度" + depths[i] + "商店出售了错误档位的枪或弓");
				seen.add(result.getClass());
			}
			check(seen.contains(bows[i]) && seen.contains(guns[i]),
					"深度" + depths[i] + "商店没有执行枪/弓二选一");
		}
	}

	private static void testLegacyIcons() throws Exception {
		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		ByteBuffer pixels = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
		for (int slot = 0; slot < ICON_HASHES.length; slot++) {
			pixels.clear();
			for (int y = 624; y < 640; y++) {
				for (int x = 128 + slot * 16; x < 144 + slot * 16; x++) pixels.putInt(sheet.getRGB(x, y));
			}
			check(ICON_HASHES[slot].equals(toHex(digest.digest(pixels.array()))),
					"第" + (slot + 1) + "个弓箭图标与旧版像素不一致");
		}
	}

	private static String toHex(byte[] bytes) {
		StringBuilder result = new StringBuilder(bytes.length * 2);
		for (byte value : bytes) result.append(String.format("%02X", value & 0xFF));
		return result.toString();
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsRangeWeaponTest() { }
}
