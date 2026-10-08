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
import pd.actors.mobs.npcs.Shopkeeper;
import pd.items.Generator;
import pd.items.Gold;
import pd.items.Heap;
import pd.items.Item;
import pd.items.StoneOre;
import pd.items.equipment.wands.Wand;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.mechanics.Ballistica;
import pd.plants.Firebloom;
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
			testStealCurve();
			testGroundAndShopLanding();
			testFailedShopTheftAlertsShopkeeper();
			testFailedHiddenShopTheftCostsPermanentHealth();
			testPoolsAndResources();
			System.out.println("SPS魔术之手法杖测试通过：施法伤害并命中偷窃、地面掉落物取来、商店货品概率偷窃"
					+ "（失手惊动老板且不散钱）、秘密商店失手扣货价一半永久生命、指数偷窃曲线与双语文本均正常。");
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
		//SPSEXPD: 独立的「魔术之手」动作已删除，一切走「释放」
		check(!staff.actions(hero).contains("MAGIC_HAND"), "魔术之手的独立动作仍然存在");
		//SPSEXPD: 弹道与雷霆法杖一致，落在指定点后停止（不再继续飞行）
		check(staff.collisionProperties(0) == Ballistica.PROJECTILE,
				"弹道没有改为落在指定点后停止：" + staff.collisionProperties(0));

		staff.curCharges = 0;
		staff.curChargeKnown = true;
		check(!staff.actions(hero).contains(Wand.AC_ZAP), "无充能且已知充能时仍开放释放动作");
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

		Ballistica bolt = new Ballistica(hero.pos, mob.pos, Ballistica.PROJECTILE);
		staff.onZap(bolt);
		check(mob.HP < 100, "施法没有对目标造成伤害：HP=" + mob.HP
				+ " target=" + (Actor.findChar(bolt.collisionPos) == mob)
				+ " collision=" + bolt.collisionPos + " heroPos=" + hero.pos + " mobPos=" + mob.pos
				+ " roll=" + staff.damageRoll());
		check(containsMarker(hero), "施法命中的目标身上没有物品被偷走");
		check(!mob.firstItem, "偷窃后没有清掉目标的 firstItem 标记");

		//第二次命中：目标身上已没有可偷的东西，应只给一块石头
		int stonesBefore = countStones(hero);
		staff.onZap(new Ballistica(hero.pos, mob.pos, Ballistica.PROJECTILE));
		check(countStones(hero) > stonesBefore || mob.HP < 100, "第二次命中没有回退为空手石块");
	}

	/** SPSEXPD: 偷窃价位随等级指数上涨——0 级 20、45 级 7000（即 45 级时标价 10000 = 70%）。 */
	private static void testStealCurve() {
		prepareHero();
		MasterThievesArmband staff = new MasterThievesArmband();

		staff.level(0);
		check(Math.abs(staff.stealValueCap() - 20f) < 0.01f,
				"0级偷窃价位上限不是20：" + staff.stealValueCap());

		staff.level(10);
		float mid = staff.stealValueCap();
		check(mid > 20f && mid < 7000f, "偷窃价位上限没有随等级指数上涨：" + mid);

		staff.level(45);
		float cap = staff.stealValueCap();
		check(Math.abs(cap - 7000f) < 1f, "45级偷窃价位上限不是7000：" + cap);
		check(cap / 10000f >= 0.7f, "45级时标价10000的商品成功率不足70%：" + cap / 10000f);

		//越贵越难：同一等级下标价更高的商品成功率更低
		staff.level(0);
		check(staff.stealChance(new PriceItem(50)) > staff.stealChance(new PriceItem(5000)),
				"更贵的商品没有更难偷");
		check(staff.stealChance(new PriceItem(1)) == 1f, "极低价商品没有必偷到");

		//免费店（标价 0）不得除零
		Shopkeeper.freeAndNoRestock = true;
		try {
			float free = staff.stealChance(new PriceItem(5000));
			check(free >= 0f && free <= 1f, "免费店标价导致成功率越界：" + free);
		} finally {
			Shopkeeper.freeAndNoRestock = false;
		}
	}

	/** SPSEXPD: 落点行为——普通掉落物取来、空地无行为、商店货品按概率偷取。 */
	private static void testGroundAndShopLanding() {
		Hero hero = prepareHero();
		TestLevel level = new TestLevel();
		Dungeon.level = level;
		hero.pos = 27;

		MasterThievesArmband staff = new MasterThievesArmband();
		staff.collect(hero.belongings.backpack);
		staff.level(45);

		//普通掉落物：直接取来
		heapAt(level, 28, Heap.Type.HEAP, new MarkerItem());
		staff.onZap(new Ballistica(hero.pos, 28, Ballistica.PROJECTILE));
		check(countMarkers(hero) == 1, "落在掉落物上没有取来东西");
		check(level.heaps.get(28) == null, "取走掉落物后原位的堆没有清掉");

		//空地：不产生任何行为
		staff.onZap(new Ballistica(hero.pos, 29, Ballistica.PROJECTILE));
		check(level.heaps.get(29) == null, "落在空地时产生了物品");

		//草丛：像踩踏一样把草踩掉（高草 → 草地）
		setTerrain(level, 26, Terrain.HIGH_GRASS);
		staff.onZap(new Ballistica(hero.pos, 26, Ballistica.PROJECTILE));
		check(level.map[26] == Terrain.GRASS, "落在高草上没有踩踏：" + level.map[26]);

		//犁过的草：踩踏后同样变回草地
		setTerrain(level, 25, Terrain.FURROWED_GRASS);
		staff.onZap(new Ballistica(hero.pos, 25, Ballistica.PROJECTILE));
		check(level.map[25] == Terrain.GRASS, "落在犁过的草上没有踩踏：" + level.map[25]);

		//野生植物：踩踏只让植物枯萎，不再触发自身的植物效果
		TestPlant plant = new TestPlant();
		plant.pos = 33;
		check(level.passable[33] && !level.solid[33], "测试落点不可通行：33");
		level.plants.put(33, plant);
		staff.onZap(new Ballistica(hero.pos, 33, Ballistica.PROJECTILE));
		check(!plant.activated, "野生植物被踩踏时仍然触发了自身的植物效果");
		check(level.plants.get(33) == null, "踩踏后植物没有被移除");

		//果丛（人工/精心培育的 Ex* 系列）：踩踏仍保留收获
		Firebloom.ExFirebloom bush = new Firebloom.ExFirebloom();
		bush.pos = 34;
		check(level.passable[34] && !level.solid[34], "测试落点不可通行：34");
		level.plants.put(34, bush);
		staff.onZap(new Ballistica(hero.pos, 34, Ballistica.PROJECTILE));
		check(level.plants.get(34) == null, "踩踏果丛后植物没有被移除");
		check(level.heaps.get(34) != null, "踩踏果丛没有留下收获（蔬菜应落在踩踏格）");

		//容器：未上锁的宝箱可以隔空打开
		Heap chest = heapAt(level, 35, Heap.Type.CHEST, new MarkerItem());
		staff.onZap(new Ballistica(hero.pos, 35, Ballistica.PROJECTILE));
		check(level.heaps.get(35) == chest, "隔空开箱后容器堆消失了");
		check(chest.type == Heap.Type.HEAP, "隔空开箱后容器类型没有变成普通堆：" + chest.type);
		check(chest.size() == 1, "隔空开箱后箱内物品被拿走了");

		//上锁的宝箱不在远程开启范围内
		check(!MasterThievesArmband.isRemoteOpenable(
						heapAt(level, 36, Heap.Type.LOCKED_CHEST, new MarkerItem())),
				"上锁的宝箱被当成了可远程开启的容器");
		check(!MasterThievesArmband.isRemoteOpenable(chest), "普通堆不该被当成容器");

		//普通商店货品：等级足够时必偷到，且不会惊动老板
		Shopkeeper.priceMultiplier = 1f;
		Item goods = new MarkerItem();
		check(staff.stealChance(goods) == 1f, "平价货品不是必偷到：" + staff.stealChance(goods));
		heapAt(level, 30, Heap.Type.FOR_SALE, goods);
		Ballistica shopBolt = new Ballistica(hero.pos, 30, Ballistica.PROJECTILE);
		staff.onZap(shopBolt);
		check(countMarkers(hero) == 2, "落在商店货品上没有偷到东西：markers=" + countMarkers(hero)
				+ " collision=" + shopBolt.collisionPos + " heap=" + level.heaps.get(30)
				+ " heroPos=" + hero.pos);
		check(level.heaps.get(30) == null, "偷走货品后原位的堆没有清掉");
		check(Shopkeeper.priceMultiplier == 1f, "偷窃成功却惊动了商店老板");
	}

	/** SPSEXPD: 普通商店偷窃失手——惊动老板，但不散落金币（与被打不同）。 */
	private static void testFailedShopTheftAlertsShopkeeper() {
		Hero hero = prepareHero();
		TestLevel level = new TestLevel();
		Dungeon.level = level;
		hero.pos = 27;

		MasterThievesArmband staff = new MasterThievesArmband();
		staff.level(0);

		Shopkeeper keeper = new Shopkeeper();
		keeper.pos = 20;
		level.mobs().add(keeper);
		level.heroFOV[keeper.pos] = true;

		//SPSEXPD: 层里再放一只怪——惊动老板会召唤守卫（往 mobs 里加人），
		//只放一个元素时 HashSet 迭代器不会再调 next()，覆盖不到
		//ConcurrentModificationException 闪退；两个元素才能复现
		TestMob bystander = new TestMob();
		bystander.pos = 21;
		level.mobs().add(bystander);

		Shopkeeper.priceMultiplier = 1f;
		int goldBefore = Dungeon.gold;

		//标价远超 0 级的价位上限，必然失手
		heapAt(level, 28, Heap.Type.FOR_SALE, new PriceItem(1000000));
		staff.onZap(new Ballistica(hero.pos, 28, Ballistica.PROJECTILE));

		check(level.heaps.get(28) != null, "偷窃失手却拿走了货品");
		check(Shopkeeper.priceMultiplier > 1f, "偷窃失手没有惊动商店老板");
		check(!hasGoldHeap(level), "偷窃失手不应散落金币");
		check(Dungeon.gold == goldBefore, "偷窃失手不应改变金币数量");

		Shopkeeper.priceMultiplier = 1f;
	}

	/** SPSEXPD: 秘密商店偷窃失手——损失「货价一半」的永久生命上限。 */
	private static void testFailedHiddenShopTheftCostsPermanentHealth() {
		Hero hero = prepareHero();
		TestLevel level = new TestLevel();
		Dungeon.level = level;
		hero.pos = 27;

		MasterThievesArmband staff = new MasterThievesArmband();
		staff.level(0);

		int htBefore = hero.permanentHT();
		int cost = Math.max(1, pd.windows.WndLifeTradeItem.price() / 2);

		heapAt(level, 28, Heap.Type.FOR_LIFE, new PriceItem(1000000));
		staff.onZap(new Ballistica(hero.pos, 28, Ballistica.PROJECTILE));

		check(level.heaps.get(28) != null, "秘密商店偷窃失手却拿走了货品");
		check(hero.permanentHT() == htBefore - cost,
				"秘密商店偷窃失手没有损失货价一半的永久生命：" + hero.permanentHT() + "/" + htBefore);

		//永久生命已不足以支付代价时保底不扣
		hero.HTBoost = -29;      //permanentHT() == 1
		hero.updateHT(false);
		heapAt(level, 34, Heap.Type.FOR_LIFE, new PriceItem(1000000));
		staff.onZap(new Ballistica(hero.pos, 34, Ballistica.PROJECTILE));
		check(hero.permanentHT() == 1, "永久生命不足时仍然被抽走了上限：" + hero.permanentHT());
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
			for (String key : new String[]{"name", "desc", "stats_desc", "stolen", "stolen_stone", "pick_ground",
					"steal_goods_ok", "steal_goods_fail", "steal_life_ok", "steal_life_fail", "steal_life_none"}) {
				required(items, "items.equipment.artifacts.masterthievesarmband." + key, file);
			}
			for (String removed : new String[]{"ac_magic_hand", "magic_prompt", "magic_none", "magic_shop",
					"magic_range", "magic_done", "ac_goldtouch"}) {
				check(items.getProperty("items.equipment.artifacts.masterthievesarmband." + removed) == null,
						file + "仍保留已删除的文案：" + removed);
			}
		}
		String zh = read("messages/items/zh/items.properties");
		check(zh.contains("魔术之手法杖"), "中文文本缺少魔术之手法杖名称");
	}

	//---- 辅助 ----

	/** SPSEXPD: 设置测试地形；同样要求落点可通行，否则弹道到不了该格。 */
	private static void setTerrain(TestLevel level, int cell, int terrain) {
		check(level.passable[cell] && !level.solid[cell], "测试落点不可通行：" + cell);
		level.map[cell] = terrain;
	}

	private static Heap heapAt(TestLevel level, int cell, Heap.Type type, Item item) {		//SPSEXPD: 8x8 测试地图的最外圈会被 buildFlagMaps 标成 solid，落点必须选可通行格
		check(level.passable[cell] && !level.solid[cell], "测试落点不可通行：" + cell);
		Heap heap = new Heap();
		heap.type = type;
		heap.pos = cell;
		heap.items.add(item);
		level.heaps.put(cell, heap);
		return heap;
	}

	private static boolean hasGoldHeap(TestLevel level) {
		for (Heap heap : level.heaps.valueList()) {
			if (heap == null) continue;
			for (Item item : heap.items) {
				if (item instanceof Gold) return true;
			}
		}
		return false;
	}

	private static int countMarkers(Hero hero) {
		int count = 0;
		for (Item item : hero.belongings) {
			if (item instanceof MarkerItem) count++;
		}
		return count;
	}

	private static boolean containsMarker(Hero hero) {
		return countMarkers(hero) > 0;
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

	/** 测试用掉落物（价值 0，商店标价取 1，任何等级都必偷到）。 */
	public static class MarkerItem extends Item { }

	/** 测试用定价物：value() 决定商店标价（sellPrice = value × 章节倍率）。 */
	public static class PriceItem extends Item {
		private final int price;
		PriceItem(int price) { this.price = price; }
		@Override public int value() { return price; }
	}

	/** 测试用植物：记录是否被踩踏以及触发者。 */
	public static class TestPlant extends Plant {
		public boolean activated = false;
		public Char triggeredBy = null;

		@Override public void activate(Char ch) {
			activated = true;
			triggeredBy = ch;
		}
	}

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
