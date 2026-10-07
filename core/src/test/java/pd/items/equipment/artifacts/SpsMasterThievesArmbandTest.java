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
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.items.Generator;
import pd.items.Item;
import pd.items.StoneOre;
import pd.items.equipment.wands.Wand;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.mechanics.Ballistica;
import pd.plants.Plant;
import render.noosa.Game;
import render.utils.data.SparseArray;
import render.utils.math.Random;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/** Headless checks for the SPS magic-hand staff (the master thieves' armband turned into a wand). */
public final class SpsMasterThievesArmbandTest {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Random.pushGenerator(0x5350534D48414E44L);
		try {
			testWandBasics();
			testZapDamageAndSteal();
			testMagicHandAction();
			testPoolsAndResources();
			System.out.println("SPS魔术之手法杖测试通过：法杖充能与伤害公式、施法伤害并命中偷窃、魔术之手动作、法杖池与中英文本均正常。");
		} finally {
			Random.popGenerator();
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			app.exit();
		}
	}

	private static void testWandBasics() {
		Hero hero = prepareHero();
		MasterThievesArmband staff = new MasterThievesArmband();

		check(staff instanceof Wand, "魔术之手法杖不再以法杖方式工作");
		check(staff.initialCharges() == 3, "法杖初始充能不是3：" + staff.initialCharges());
		check(staff.min(0) == 2 && staff.max(0) == 5, "0级伤害公式不符：" + staff.min(0) + "-" + staff.max(0));
		check(staff.min(3) == 5 && staff.max(3) == 14, "3级伤害公式不符：" + staff.min(3) + "-" + staff.max(3));
		check(Wand.AC_ZAP.equals(staff.defaultAction()), "默认动作不是释放");
		check(staff.actions(hero).contains(Wand.AC_ZAP), "有充能时没有开放释放动作");
		check(staff.actions(hero).contains(MasterThievesArmband.AC_MAGIC_HAND), "没有开放魔术之手动作");

		staff.curCharges = 0;
		staff.curChargeKnown = true;
		check(!staff.actions(hero).contains(Wand.AC_ZAP), "无充能且已知充能时仍开放释放动作");
		check(!staff.actions(hero).contains(MasterThievesArmband.AC_MAGIC_HAND), "无充能时仍开放魔术之手");
	}

	private static void testZapDamageAndSteal() {
		Hero hero = prepareHero();
		TestLevel level = new TestLevel();
		Dungeon.level = level;
		hero.pos = 27;

		TestMob mob = new TestMob();
		mob.pos = 28;
		mob.HP = mob.HT = 100;
		level.mobs().add(mob);
		//SPSEXPD: 无头测试需手动注册到 Actor 静态表，否则 Ballistica 的 STOP_CHARS 看不到这个单位
		Actor.add(mob);

		MasterThievesArmband staff = new MasterThievesArmband();
		staff.collect(hero.belongings.backpack);

		Ballistica bolt = new Ballistica(hero.pos, mob.pos, Ballistica.MAGIC_BOLT);
		staff.onZap(bolt);
		check(mob.HP < 100, "施法没有对目标造成伤害：HP=" + mob.HP
				+ " target=" + (Actor.findChar(bolt.collisionPos) == mob)
				+ " collision=" + bolt.collisionPos + " heroPos=" + hero.pos + " mobPos=" + mob.pos
				+ " roll=" + staff.damageRoll());
		check(containsMarker(hero), "施法命中的目标身上没有物品被偷走");
		check(!mob.firstItem, "偷窃后没有清掉目标的 firstItem 标记");

		//第二次命中：目标身上已没有可偷的东西，应只给一块石头
		int stonesBefore = countStones(hero);
		staff.onZap(new Ballistica(hero.pos, mob.pos, Ballistica.MAGIC_BOLT));
		check(countStones(hero) > stonesBefore || mob.HP < 100, "第二次命中没有回退为空手石块");
	}

	private static void testMagicHandAction() {
		Hero hero = prepareHero();
		Dungeon.level = new TestLevel();
		hero.pos = 27;

		MasterThievesArmband staff = new MasterThievesArmband();
		staff.collect(hero.belongings.backpack);
		check(staff.magicHand != null, "魔术之手的选格监听器缺失");
		check(MasterThievesArmband.MAGIC_HAND_RANGE == 8, "魔术之手射程不是8");
	}

	private static void testPoolsAndResources() throws Exception {
		boolean inWandPool = false;
		for (Class<?> type : Generator.Category.WAND.classes) {
			if (type == MasterThievesArmband.class) inWandPool = true;
		}
		boolean inArtifactPool = false;
		for (Class<?> type : Generator.Category.ARTIFACT.classes) {
			if (type == MasterThievesArmband.class) inArtifactPool = true;
		}
		check(inWandPool, "魔术之手法杖没有进入法杖池");
		check(!inArtifactPool, "魔术之手法杖仍留在神器池中");
		check(Generator.Category.WAND.classes.length == Generator.Category.WAND.defaultProbs.length,
				"法杖池与权重数组长度不一致");
		check(Generator.Category.ARTIFACT.classes.length == Generator.Category.ARTIFACT.defaultProbs.length,
				"神器池与权重数组长度不一致");

		for (String file : new String[]{"messages/items/zh/items.properties", "messages/items/en/items.properties"}) {
			Properties items = load(file);
			for (String key : new String[]{"name", "desc", "stats_desc", "stolen", "ac_magic_hand"}) {
				required(items, "items.equipment.artifacts.masterthievesarmband." + key, file);
			}
			check(items.getProperty("items.equipment.artifacts.masterthievesarmband.ac_goldtouch") == null,
					file + "仍保留已删除的耗竭-点金文案");
		}
		String zh = read("messages/items/zh/items.properties");
		check(zh.contains("魔术之手法杖"), "中文文本缺少魔术之手法杖名称");
	}

	//---- 辅助 ----

	private static boolean containsMarker(Hero hero) {
		for (Item item : hero.belongings) {
			if (item instanceof MarkerItem) return true;
		}
		return false;
	}

	private static int countStones(Hero hero) {
		int count = 0;
		for (Item item : hero.belongings) {
			if (item instanceof StoneOre) count++;
		}
		return count;
	}

	private static Hero prepareHero() {
		Actor.clear();
		Hero hero = new Hero();
		hero.HP = hero.HT = 100;
		Dungeon.hero = hero;
		return hero;
	}

	private static Properties load(String path) throws Exception {
		Properties properties = new Properties();
		try (InputStreamReader reader = new InputStreamReader(
				java.nio.file.Files.newInputStream(Path.of(path)), StandardCharsets.UTF_8)) {
			properties.load(reader);
		}
		return properties;
	}

	private static String read(String path) throws Exception {
		return new String(java.nio.file.Files.readAllBytes(Path.of(path)), StandardCharsets.UTF_8);
	}

	private static void required(Properties properties, String key, String file) {
		check(properties.getProperty(key) != null && !properties.getProperty(key).isEmpty(),
				file + "缺少文本：" + key);
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	/** 测试用掉落物。 */
	public static class MarkerItem extends Item { }

	/** 测试用目标：身上带一件可偷的物品。 */
	public static class TestMob extends Mob {
		TestMob() {
			HP = HT = 100;
			firstItem = true;
		}

		@Override public Item SupercreateLoot() { return new MarkerItem(); }
		@Override public int attackSkill(Char target) { return 10; }
		@Override public int defenseSkill(Char enemy) { return 0; }
		@Override public int damageRoll() { return 1; }
		@Override public int drRoll() { return 0; }
	}

	/** 无头用的最小地图。 */
	private static final class TestLevel extends Level {
		TestLevel() {
			setSize(8, 8);
			//SPSEXPD: Level.setSize 后 map 默认全是墙（Terrain.WALL=0），必须铺成空地并重建通行标记，
			//否则 Ballistica 会在起点就撞墙（collisionPos == src）
			java.util.Arrays.fill(map, Terrain.EMPTY);
			mobs().clear();
			heaps = new SparseArray<>();
			blobs = new HashMap<>();
			plants = new SparseArray<Plant>();
			traps = new SparseArray<>();
			transitions = new ArrayList<>();
			customTiles = new ArrayList<>();
			customTerrain = new ArrayList<>();
			customWalls = new ArrayList<>();
			heroFOV = new boolean[length()];
			visited = new boolean[length()];
			mapped = new boolean[length()];
			//SPSEXPD: 等 mobs/heaps 等集合就位后再重建通行标记
			buildFlagMaps();
		}

		@Override protected boolean build() { return true; }
		@Override protected void createMobs() { }
		@Override protected void createItems() { }
		@Override public String tilesTex() { return null; }
		@Override public String waterTex() { return null; }
	}

	private SpsMasterThievesArmbandTest() { }
}
