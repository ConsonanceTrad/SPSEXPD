package pd.items.wands;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.ArmorBreak;
import pd.actors.buffs.Bleeding;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.actors.mobs.npcs.TownNpc;
import pd.items.CrystalVial;
import pd.items.Item;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.mechanics.Ballistica;
import pd.plants.Plant;
import pd.sprites.CatSheepSprite;
import pd.sprites.ItemSpriteSheet;
import render.utils.data.SparseArray;
import render.utils.serialize.Bundle;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

public final class SpsTownMagicTest {

	private static final String CRYSTAL_ICON_HASH =
			"A669CE73B3B71F8EAB7C033B27FEBB25FA287431768E1B8CFDE0743A2385138B";

	public static void main(String[] args) throws Exception {
		try {
			testNpcSourcesAndProperties();
			testCrystalVial();
			testBlackMeowWand();
			testBloodMoonWand();
			testShatteredFireblast();
			testIconsAndSprite();
			System.out.println("SPS城镇魔法奖励测试通过：水晶瓶、三根法杖、NPC来源、状态、存档、边界和原始图像均正常。");
		} finally {
			Actor.clear();
			Dungeon.level = null;
			Dungeon.hero = null;
			Dungeon.dewDraw = false;
		}
	}

	private static void testNpcSourcesAndProperties() {
		TownNpc blackMeow = new TownNpc().configure(TownNpc.Spec.BLACK_MEOW);
		TownNpc boneStar = new TownNpc().configure(TownNpc.Spec.BONE_STAR);
		TownNpc ice13 = new TownNpc().configure(TownNpc.Spec.ICE13);
		TownNpc sfb = new TownNpc().configure(TownNpc.Spec.SFB);
		check(blackMeow.SupercreateLoot() instanceof WandOfBlackMeow
				&& blackMeow.properties().contains(Char.Property.BEAST), "BlackMeow奖励或属性错误");
		check(boneStar.SupercreateLoot() instanceof CrystalVial
				&& boneStar.properties().contains(Char.Property.HUMAN), "BoneStar奖励或属性错误");
		check(ice13.SupercreateLoot() instanceof WandOf13
				&& ice13.properties().contains(Char.Property.HUMAN), "Ice13奖励或属性错误");
		check(sfb.SupercreateLoot() instanceof WandOfShatteredFireblast
				&& sfb.properties().contains(Char.Property.UNKNOW), "SFB奖励或属性错误");
	}

	private static void testCrystalVial() {
		Hero hero = new Hero();
		hero.HT = 100;
		hero.HP = 50;
		CrystalVial vial = new CrystalVial();
		for (int i = 0; i < 10; i++) vial.fill();
		check(vial.volume() == 50, "水晶瓶充能上限过程错误");
		vial.fill();
		check(vial.volume() == 50, "水晶瓶满50后仍继续充能");
		vial.volume(51);
		check(vial.actions(hero).contains("DRINK") && vial.actions(hero).contains("BLESS"),
				"水晶瓶动作阈值错误");
		check(vial.drink(hero) && hero.HP == 70 && vial.volume() == 41, "水晶瓶恢复量或消耗错误");
		vial.volume(54);
		vial.fill();
		check(vial.volume() == 54, "旧版水晶瓶超过50后的停止充能行为错误");
		TestItem item = new TestItem();
		check(vial.blessItem(hero, item) && item.level() == 1 && vial.volume() == 4,
				"水晶瓶强化或50点消耗错误");

		vial.volume(47);
		Bundle bundle = new Bundle();
		vial.storeInBundle(bundle);
		CrystalVial restored = new CrystalVial();
		restored.restoreFromBundle(bundle);
		check(restored.volume() == 47 && restored.isIdentified() && !restored.isUpgradable(),
				"水晶瓶存档或基础属性错误");
	}

	private static void testBlackMeowWand() {
		Actor.clear();
		level();
		hero(27);
		WandOfBlackMeow wand = new WandOfBlackMeow();
		check(wand.image == ItemSpriteSheet.WAND_FLOCK, "灵猫法杖图标槽位错误");
		check(wand.findSpawnCell(28) == 28, "灵猫法杖没有选择空目标格");
		PlainMob blocker = new PlainMob();
		blocker.pos = 28;
		Actor.add(blocker);
		check(wand.findSpawnCell(28) >= 0 && wand.findSpawnCell(28) != 28,
				"灵猫法杖没有避开被占据的目标格");
		check(WandOfBlackMeow.retaliationMax(0) == 0
				&& WandOfBlackMeow.retaliationMax(5) == 15, "灵猫反击伤害成长错误");
		WandOfBlackMeow.MagicMeow cat = new WandOfBlackMeow.MagicMeow();
		cat.initialize(7f, 5);
		Bundle bundle = new Bundle();
		cat.storeInBundle(bundle);
		WandOfBlackMeow.MagicMeow restored = new WandOfBlackMeow.MagicMeow();
		restored.restoreFromBundle(bundle);
		check(restored.lifespan() == 7f && restored.wandLevel() == 5
				&& restored.properties().contains(Char.Property.UNKNOW), "灵猫存档、寿命或属性错误");
	}

	private static void testBloodMoonWand() {
		Actor.clear();
		level();
		Hero hero = hero(27);
		PlainMob target = new PlainMob();
		target.pos = 29;
		target.HP = target.HT = 1000;
		Actor.add(target);
		WandOf13 wand = new WandOf13();
		wand.level(5);
		wand.onZap(new Ballistica(hero.pos, 31, Ballistica.WONT_STOP));
		check(wand.min(0) == 0 && wand.max(0) == 1 && wand.min(5) == 5 && wand.max(5) == 11,
				"血色月华伤害成长错误");
		check(WandOf13.maxDistance(0) == 1 && WandOf13.maxDistance(20) == 10
				&& WandOf13.damageLevel(5, 2) == 3, "血色月华射程或穿透衰减错误");
		check(target.buff(Bleeding.class) != null && target.buff(ArmorBreak.class) != null
				&& target.buff(ArmorBreak.class).level() == 20, "血色月华没有施加流血和20级破甲");
	}

	private static void testShatteredFireblast() {
		Actor.clear();
		level();
		Hero hero = hero(9);
		WandOfShatteredFireblast wand = new WandOfShatteredFireblast();
		check(wand.image == ItemSpriteSheet.WAND_SPS_FIREBOLT, "破碎暴风火杖图标槽位错误");
		check(WandOfShatteredFireblast.chargeCost(1) == 1
				&& WandOfShatteredFireblast.chargeCost(10) == 3
				&& WandOfShatteredFireblast.maximumDistance(3) == 9,
				"破碎暴风火杖充能或射程公式错误");
		wand.curCharges = 10;
		Set<Integer> cells = wand.prepareFlameCells(new Ballistica(hero.pos, 14, Ballistica.STOP_SOLID));
		check(!cells.isEmpty(), "破碎暴风火杖没有生成火焰波范围");
		for (int cell : cells) check(Dungeon.level.insideMap(cell), "破碎暴风火杖越过地图边界");
		check(Math.abs(WandOfShatteredFireblast.castMultiplier(3) - 2.25f) < 0.0001f,
				"破碎暴风火杖多充能伤害倍率错误");
	}

	private static void testIconsAndSprite() throws Exception {
		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		ByteBuffer pixels = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
		for (int y = 880; y < 896; y++) for (int x = 112; x < 128; x++) pixels.putInt(sheet.getRGB(x, y));
		byte[] digest = MessageDigest.getInstance("SHA-256").digest(pixels.array());
		StringBuilder actual = new StringBuilder(64);
		for (byte value : digest) actual.append(String.format("%02X", value & 0xFF));
		check(CRYSTAL_ICON_HASH.equals(actual.toString()), "水晶瓶图标与旧版像素不一致：" + actual);
		check(new CrystalVial().image == ItemSpriteSheet.CRYSTAL_VIAL, "水晶瓶没有使用原图槽位");
		check(CatSheepSprite.class != null && new File("sprites/npcs/sps_town_catsheep.png").isFile(),
				"灵猫动画图集缺失");
	}

	private static TestLevel level() {
		TestLevel level = new TestLevel();
		Dungeon.level = level;
		Arrays.fill(level.map, Terrain.EMPTY);
		Arrays.fill(level.passable, true);
		return level;
	}

	private static Hero hero(int pos) {
		Hero hero = new Hero();
		hero.pos = pos;
		hero.HP = hero.HT = 100;
		Dungeon.hero = hero;
		Actor.add(hero);
		return hero;
	}

	private static final class PlainMob extends Mob {
		{ alignment = Alignment.ENEMY; }
	}

	private static final class TestItem extends Item {
		@Override public boolean isUpgradable() { return true; }
	}

	private static final class TestLevel extends Level {
		TestLevel() {
			setSize(8, 8);
			mobs().clear();
			heaps = new SparseArray<>();
			blobs = new HashMap<>();
			plants = new SparseArray<Plant>();
			traps = new SparseArray<>();
			transitions = new ArrayList<>();
			customTiles = new ArrayList<>();
			customTerrain = new ArrayList<>();
			customWalls = new ArrayList<>();
		}
		@Override protected boolean build() { return true; }
		@Override protected void createMobs() { }
		@Override protected void createItems() { }
		@Override public String tilesTex() { return null; }
		@Override public String waterTex() { return null; }
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsTownMagicTest() { }
}
