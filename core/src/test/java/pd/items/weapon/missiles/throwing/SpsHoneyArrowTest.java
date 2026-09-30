package pd.items.weapon.missiles.throwing;

import pd.Dungeon;
import pd.Statistics;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Poison;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.actors.mobs.Bee;
import pd.actors.mobs.Mob;
import pd.actors.mobs.npcs.TownNpc;
import pd.items.Honeypot;
import pd.items.Item;
import pd.items.quest.AdventureJournal;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.plants.Plant;
import pd.sprites.ItemSpriteSheet;
import render.utils.data.SparseArray;
import render.utils.serialize.Bundle;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import javax.imageio.ImageIO;

public final class SpsHoneyArrowTest {

	private static final String ICON_HASH =
			"09087F9CA15CD1FC322C20C2CA652355AC2C632F802533479FD8638858A3A12B";
	private static final String BEE_SHEET_HASH =
			"28E1FD595FBD66ECCFDD49A6A294491C2557E438AD7477442317255C0FBE5F3B";

	public static void main(String[] args) throws Exception {
		int oldDepth = Dungeon.depth;
		int oldBranch = Dungeon.branch;
		int oldDeepest = Statistics.deepestFloor;
		try {
			testItemAndNpcSource();
			testOrdinaryBeeBurst();
			testLeaderBeeBurstAndPot();
			testSteelBeePersistence();
			testAssets();
			System.out.println("SPS蜜蜂针头测试通过：四向召唤、领袖钢蜂、蜜罐联动、NPC来源、存档和原始图像均正常。");
		} finally {
			Actor.clear();
			Dungeon.level = null;
			Dungeon.hero = null;
			Dungeon.depth = oldDepth;
			Dungeon.branch = oldBranch;
			Statistics.deepestFloor = oldDeepest;
		}
	}

	private static void testItemAndNpcSource() {
		HoneyArrow arrow = (HoneyArrow)new TownNpc().configure(TownNpc.Spec.HONEY_POOOOT).SupercreateLoot();
		check(arrow.quantity() == 3 && arrow.min() == 1 && arrow.max() == 1 && arrow.STRReq() == 10,
				"HoneyPoooot掉落数量或蜜蜂针头数值错误");
		check(!arrow.isUpgradable() && arrow.isIdentified() && arrow.value() == 60
				&& arrow.image == ItemSpriteSheet.HONEY_ARROW, "蜜蜂针头基础属性、售价或图标错误");
		HoneyArrow generated = new HoneyArrow();
		generated.random();
		check(generated.quantity() == 1, "旧版蜜蜂针头随机数量行为错误");
	}

	private static void testOrdinaryBeeBurst() {
		Actor.clear();
		TestLevel level = level();
		Hero hero = hero(18, HeroSubClass.NONE);
		PlainMob target = new PlainMob();
		target.pos = 28;
		target.HP = target.HT = 100;
		Actor.add(target);
		new HoneyArrow().proc(hero, target, 1);
		check(level.mobs().size() == 4, "蜜蜂针头命中后没有在四个方向召唤蜜蜂");
		for (Mob mob : level.mobs()) check(mob instanceof Bee && mob.HP == mob.HT
				&& mob.HT == 120 && ((Bee)mob).beeLevel() == 10,
				"非领袖蜜蜂针头召唤了错误单位、生命或深度");
	}

	private static void testLeaderBeeBurstAndPot() {
		Actor.clear();
		TestLevel level = level();
		Hero hero = hero(18, HeroSubClass.LEADER);
		PlainMob target = new PlainMob();
		target.pos = 28;
		target.HP = target.HT = 100;
		Actor.add(target);
		new HoneyArrow().proc(hero, target, 1);
		check(level.mobs().size() == 4, "领袖蜜蜂针头命中后召唤数量错误");
		for (Mob mob : level.mobs()) check(mob instanceof Honeypot.SteelBee
				&& mob.alignment == Char.Alignment.ALLY, "领袖蜜蜂针头没有召唤友方钢蜂");

		level.mobs().clear();
		Item result = new Honeypot().shatter(hero, 29);
		check(result instanceof Honeypot.ShatteredPot && level.mobs().size() == 1
				&& level.mobs().iterator().next() instanceof Honeypot.SteelBee,
				"领袖打碎普通蜜罐时没有恢复钢蜂分支");
	}

	private static void testSteelBeePersistence() {
		Statistics.deepestFloor = 12;
		Honeypot.SteelBee bee = new Honeypot.SteelBee();
		bee.spawn(20);
		check(bee.beeLevel() == 12 && bee.HT == 280 && bee.attackSkill(null) == 70,
				"钢蜂没有保留旧版存档深度封顶与当次属性成长差异");
		check(bee.damageRoll() >= bee.HT / 8 && bee.damageRoll() <= bee.HT / 2
				&& bee.isImmune(Poison.class), "钢蜂伤害区间或毒素免疫错误");
		Bundle bundle = new Bundle();
		bee.storeInBundle(bundle);
		Honeypot.SteelBee restored = new Honeypot.SteelBee();
		restored.restoreFromBundle(bundle);
		check(restored.beeLevel() == 12 && restored.HT == 248 && restored.HP == 248,
				"钢蜂存档后等级或生命错误");

		Actor.clear();
		TestLevel level = level();
		Dungeon.depth = 14;
		Dungeon.branch = AdventureJournal.branchFor(22);
		Statistics.deepestFloor = 25;
		Hero hero = hero(18, HeroSubClass.NONE);
		Bee chaosBee = (Bee)new HoneyArrow().createBee();
		check(chaosBee.beeLevel() == 25 && chaosBee.HT == 420
				&& chaosBee.attackSkill(null) == 94,
				"混沌路线普通蜜蜂没有使用旧版85层生成数值");
		hero.subClass = HeroSubClass.LEADER;
		Honeypot.SteelBee chaosSteel = (Honeypot.SteelBee)new HoneyArrow().createBee();
		check(chaosSteel.beeLevel() == 25 && chaosSteel.HT == 540
				&& chaosSteel.attackSkill(null) == 200,
				"混沌路线钢蜂没有使用旧版85层生成数值");
		check(level.mobs().isEmpty(), "纯召唤数值测试错误加入了地图角色");
	}

	private static void testAssets() throws Exception {
		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		ByteBuffer pixels = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
		for (int y = 800; y < 816; y++) for (int x = 224; x < 240; x++) pixels.putInt(sheet.getRGB(x, y));
		check(ICON_HASH.equals(hex(MessageDigest.getInstance("SHA-256").digest(pixels.array()))),
				"蜜蜂针头图标与旧版像素不一致");
		check(BEE_SHEET_HASH.equals(hex(MessageDigest.getInstance("SHA-256")
				.digest(Files.readAllBytes(new File("sprites/pets/bee.png").toPath())))),
				"钢蜂动画图集与旧版文件不一致");
	}

	private static String hex(byte[] bytes) {
		StringBuilder result = new StringBuilder(bytes.length * 2);
		for (byte value : bytes) result.append(String.format("%02X", value & 0xFF));
		return result.toString();
	}

	private static TestLevel level() {
		TestLevel level = new TestLevel();
		Dungeon.level = level;
		Dungeon.depth = 10;
		Statistics.deepestFloor = 10;
		Arrays.fill(level.map, Terrain.EMPTY);
		Arrays.fill(level.passable, true);
		return level;
	}

	private static Hero hero(int pos, HeroSubClass subClass) {
		Hero hero = new Hero();
		hero.pos = pos;
		hero.HP = hero.HT = 100;
		hero.subClass = subClass;
		Dungeon.hero = hero;
		Actor.add(hero);
		return hero;
	}

	private static final class PlainMob extends Mob {
		{ alignment = Alignment.ENEMY; }
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

	private SpsHoneyArrowTest() { }
}
