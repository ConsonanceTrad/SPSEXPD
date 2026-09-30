package pd.levels;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.actors.mobs.npcs.GiftNpc;
import pd.actors.mobs.npcs.ImpShopkeeper;
import pd.actors.mobs.npcs.Shopkeeper;
import pd.items.Ankh;
import pd.items.DolyaSlate;
import pd.items.Generator;
import pd.items.Heap;
import pd.items.Honeypot;
import pd.items.Item;
import pd.items.PocketBall;
import pd.items.artifacts.fusion.NoomlinCrown;
import pd.items.bags.PotionBandolier;
import pd.items.bags.ScrollHolder;
import pd.items.bags.MagicalHolster;
import pd.items.challengelists.CourageChallenge;
import pd.items.challengelists.PowerChallenge;
import pd.items.challengelists.WisdomChallenge;
import pd.items.eggs.Egg;
import pd.items.food.staplefood.Pasty;
import pd.items.journalpages.Town;
import pd.items.potions.Potion;
import pd.items.quest.Mushroom;
import pd.items.rings.Ring;
import pd.items.scrolls.Scroll;
import pd.items.summon.ActiveMrDestructo;
import pd.items.summon.FairyCard;
import pd.items.summon.Mobile;
import pd.items.weapon.guns.GunA;
import pd.items.weapon.guns.GunB;
import pd.items.weapon.guns.GunC;
import pd.items.weapon.guns.GunD;
import pd.items.weapon.guns.GunE;
import pd.items.weapon.melee.special.MeleePan;
import pd.items.weapon.missiles.arrows.MagicHand;
import pd.items.weapon.missiles.fusion.RocketMissile;
import pd.items.weapon.ranges.AlloyBowN;
import pd.items.weapon.ranges.MetalBowN;
import pd.items.weapon.ranges.PVCBowN;
import pd.items.weapon.ranges.StoneBowN;
import pd.items.weapon.ranges.WoodenBowN;
import pd.plants.Plant;
import pd.tiles.CustomTilemap;
import pd.tiles.custom.SpsLegacyLevelVisual;
import render.noosa.Game;
import render.utils.Random;
import render.utils.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/** Draws complete SPS transition floors and validates their legacy visual layer. */
public final class SpsBetweenLevelTest {

	private static final int[] DEPTHS = {0, 6, 11, 16, 21};   //SPS: 0 层为特殊初始层，其余过渡层照旧
	private static final int SEEDS_PER_DEPTH = 50;

	public static void main(String[] args) {
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		try {
			runTests();
		} finally {
			app.exit();
		}
	}

	private static void runTests() {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Dungeon.hero = new Hero();
		Potion.initColors();
		Scroll.initLabels();
		Ring.initGems();
		Generator.fullReset();

		int generated = 0;
		for (int depth : DEPTHS) {
			for (int seed = 0; seed < SEEDS_PER_DEPTH; seed++) {
				Dungeon.depth = depth;
				Dungeon.branch = 0;
				Random.pushGenerator(0x42545745454E4C56L + depth * 1_000L + seed);
				try {
					BetweenLevel level = new BetweenLevel();
					check(buildWithRetries(level), depth, seed, "地图重试100次后仍构建失败");
					level.buildFlagMaps();
					validate(level, depth, seed);
					generated++;
				} finally {
					Random.popGenerator();
				}
			}
		}
		System.out.println("SPS过渡层完整地图测试通过：" + generated
				+ "张地图，旧版商店、地砖、水面、告示牌与动态视觉同步均有效。");
	}

	private static boolean buildWithRetries(BetweenLevel level) {
		for (int attempt = 0; attempt < 100; attempt++) {
			prepare(level);
			if (level.build()) return true;
		}
		return false;
	}

	private static void prepare(BetweenLevel level) {
		level.transitions = new ArrayList<>();
		level.mobs = new HashSet<>();
		level.heaps = new SparseArray<Heap>();
		level.blobs = new HashMap<>();
		level.plants = new SparseArray<Plant>();
		level.traps = new SparseArray<>();
		level.customTiles = new ArrayList<>();
		level.customTerrain = new ArrayList<>();
		level.customWalls = new ArrayList<>();
	}

	private static void validate(BetweenLevel level, int depth, int seed) {
		check(level.width() <= 48 && level.height() <= 48, depth, seed,
				"地图超出旧版48x48边界：" + level.width() + "x" + level.height());
		check(count(level.map, Terrain.SIGN) == 1, depth, seed, "入口告示牌数量不是1");
		check(count(level.map, Terrain.SECRET_DOOR) == 0, depth, seed, "仍有隐藏门");
		int residents = 0;
		for (pd.actors.mobs.Mob mob : level.mobs) {
			if (mob instanceof GiftNpc) residents++;
			check(mob.alignment != pd.actors.Char.Alignment.ENEMY,
					depth, seed, "过渡层生成了普通敌人");
		}
		check(residents == 1, depth, seed, "商店层帐篷礼物居民数量不是1");
		validateLegacyShop(level, depth, seed);

		SpsLegacyLevelVisual visual = null;
		for (CustomTilemap tile : level.customTiles) {
			if (tile instanceof SpsLegacyLevelVisual) {
				check(visual == null, depth, seed, "存在多个整图旧版视觉层");
				visual = (SpsLegacyLevelVisual) tile;
			}
		}
		check(visual != null, depth, seed, "缺少整图旧版视觉层");
		for (int cell = 0; cell < level.length(); cell++) {
			int expected = SpsLegacyLevelVisual.terrainVisual(level.map[cell]);
			check(expected >= 0 && expected < 64, depth, seed,
					"旧版图集索引越界：cell=" + cell + ", visual=" + expected);
			check(visual.visualAt(cell) == expected, depth, seed,
					"旧版视觉层与地图不同步：cell=" + cell);
		}

		int exit = level.exit();
		Level.set(exit, Terrain.LOCKED_EXIT, level);
		check(visual.visualAt(exit) == 26, depth, seed, "锁定出口视觉没有同步");
		Level.set(exit, Terrain.UNLOCKED_EXIT, level);
		check(visual.visualAt(exit) == 27, depth, seed, "解锁出口视觉没有同步");
	}

	private static void validateLegacyShop(BetweenLevel level, int depth, int seed) {
		Shopkeeper keeper = null;
		for (Mob mob : level.mobs) {
			if (mob instanceof Shopkeeper) {
				check(keeper == null, depth, seed, "商店生成了多名商人");
				keeper = (Shopkeeper)mob;
			}
		}
		check(keeper != null, depth, seed, "商店没有生成商人");
		if (depth == 21) {
			check(keeper instanceof ImpShopkeeper, depth, seed, "第21层没有使用小恶魔商人");
			check(level.map[keeper.pos] == Terrain.WATER || level.map[keeper.pos] == Terrain.OLD_HIGH_GRASS,
					depth, seed, "第21层小恶魔商人格没有生成旧版水面或水草");
		} else {
			check(keeper.getClass() == Shopkeeper.class, depth, seed, "第" + depth + "层错误使用小恶魔商人");
		}

		ArrayList<Item> stock = new ArrayList<>();
		for (Heap heap : level.heaps.valueList()) {
			if (heap.type == Heap.Type.FOR_SALE) {
				check(heap.items.size() == 1, depth, seed, "商店货位没有保持单件商品");
				stock.addAll(heap.items);
			}
		}
		//SPS: 种子包已取消（绒布袋替代，用户裁决 2026-09-28）；0 层商店新增任务蘑菇（10 金），基数 18→19
		int minimum = depth == 0 ? 19 : depth == 16 ? 16 : depth == 21 ? 18 : 17;
		check(stock.size() == minimum || stock.size() == minimum + 1, depth, seed,
				"商店商品总数不符合旧版可选宠物蛋分支：" + stock.size());
		check(count(stock, Ankh.class) == 1, depth, seed, "商店没有固定出售十字架");
		check(count(stock, RocketMissile.class) >= 1, depth, seed,
				"商店没有固定出售火箭弹，实际商品=" + stockClasses(stock));
		check(quantity(stock, MagicHand.class) >= 5, depth, seed, "商店缺少固定的5个魔术手");
		check(countSummon(stock) == 1, depth, seed, "商店召唤物五选一数量错误");
		check(count(stock, Egg.class) <= 1, depth, seed, "商店生成了多于一个宠物蛋");

		Class<?>[] bows = {WoodenBowN.class, StoneBowN.class, MetalBowN.class, AlloyBowN.class, PVCBowN.class};
		Class<?>[] guns = {GunA.class, GunB.class, GunC.class, GunD.class, GunE.class};
		int chapter = depth == 0 ? 0 : depth == 6 ? 1 : depth == 11 ? 2 : depth == 16 ? 3 : 4;
		check(count(stock, bows[chapter]) + count(stock, guns[chapter]) >= 1, depth, seed,
				"章节枪械或弓档位错误");

		if (depth == 0) {
			//SPS: 0 层商店固定出售任务蘑菇（10 金币）+ 专属商品
			check(count(stock, MeleePan.class) >= 1
					&& count(stock, Pasty.class) >= 1 && count(stock, NoomlinCrown.class) >= 1
					&& count(stock, Mushroom.class) >= 1,
					depth, seed, "0层商店专属商品不完整（应含任务蘑菇）");
		} else if (depth == 6) {
			check(count(stock, ScrollHolder.class) == 1 && count(stock, DolyaSlate.class) == 1,
					depth, seed, "第6层商店专属商品不完整");
		} else if (depth == 11) {
			check(count(stock, PotionBandolier.class) == 1 && count(stock, Town.class) == 1,
					depth, seed, "第11层商店专属商品不完整");
		} else if (depth == 16) {
			//SPS: 法器包与魔法套筒已合并（用户裁决 2026-09-28），商店出售魔法套筒
			check(count(stock, MagicalHolster.class) == 1, depth, seed, "第16层商店缺少魔法套筒");
		} else {
			check(count(stock, CourageChallenge.class) == 1 && count(stock, PowerChallenge.class) == 1
					&& count(stock, WisdomChallenge.class) == 1,
					depth, seed, "第21层商店缺少三本挑战书");
		}
	}

	private static int count(ArrayList<Item> items, Class<?> type) {
		int result = 0;
		for (Item item : items) if (type.isInstance(item)) result++;
		return result;
	}

	private static int quantity(ArrayList<Item> items, Class<?> type) {
		int result = 0;
		for (Item item : items) if (type.isInstance(item)) result += item.quantity();
		return result;
	}

	private static int countSummon(ArrayList<Item> items) {
		int result = 0;
		for (Item item : items) {
			if (item instanceof ActiveMrDestructo || item instanceof FairyCard || item instanceof Mobile
					|| item instanceof Honeypot || item instanceof PocketBall) result++;
		}
		return result;
	}

	private static String stockClasses(ArrayList<Item> items) {
		StringBuilder result = new StringBuilder();
		for (Item item : items) {
			if (result.length() > 0) result.append(',');
			result.append(item.getClass().getSimpleName());
		}
		return result.toString();
	}

	private static int count(int[] map, int terrain) {
		int count = 0;
		for (int value : map) if (value == terrain) count++;
		return count;
	}

	private static void check(boolean condition, int depth, int seed, String message) {
		if (!condition) throw new AssertionError("深度" + depth + "，种子" + seed + "：" + message);
	}

	private SpsBetweenLevelTest() {
	}
}
