package pd.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.GamesInProgress;
import pd.ShatteredPixelDungeon;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.actors.mobs.Mob;
import pd.actors.mobs.TestMob;
import pd.actors.mobs.npcs.Leadercn;
import pd.actors.mobs.npcs.Tinkerer1;
import pd.items.Gold;
import pd.items.Heap;
import pd.items.KnowledgeBook;
import pd.items.PuddingCup;
import pd.items.StoneOre;
import pd.items.VioletDewdrop;
import pd.items.armor.specialarmor.TestArmor;
import pd.items.bombs.DungeonBomb;
import pd.items.food.SmallMeat;
import pd.items.keys.IronKey;
import pd.items.potions.PotionOfMending;
import pd.items.potions.PotionOfMindVision;
import pd.items.scrolls.ScrollOfMagicMapping;
import pd.items.wands.WandOfTest;
import pd.items.weapon.melee.special.TestWeapon;
import pd.levels.traps.bufftrap.FireBuffTrap;
import pd.scenes.InterlevelScene;
import pd.tiles.custom.SpsLegacyLevelVisual;
import render.noosa.Game;
import render.utils.serialize.Bundle;

import java.io.IOException;
import java.util.Arrays;

public final class SpsLearnLevelTest {

	public static void main(String[] args) {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		new ShatteredPixelDungeon(null);
		Game.version = "test";
		testExactLayout();
		testHiddenBeginnerClass();
		testLearnLevelContent();
		testUnusedStartLevel();
		testGuideRewardsAndPersistence();
		check(Arrays.asList(InterlevelScene.Mode.values()).contains(InterlevelScene.Mode.LEARN),
				"教学加载模式缺失");
		System.out.println("SPS新手教程测试通过：48x48固定地图、隐藏新手职业、导师链、物品、陷阱和独立路线均符合旧版。");
	}

	private static void testExactLayout() {
		check(LearnRoomLayouts.LEARN_ROOM.length == 2304, "教学地图格数错误");
		check(Arrays.hashCode(LearnRoomLayouts.LEARN_ROOM) == 431741786, "教学地图逐格数据与旧版不符");
		check(Arrays.equals(LearnRoomLayouts.LEARN_ROOM, LearnRoomLayouts.START_ROOM),
				"旧版教学地图和起始地图应完全相同");
		check(LearnRoomLayouts.LEARN_ROOM != LearnRoomLayouts.START_ROOM, "两张地图不得共享可变数组");
		check(count(LearnRoomLayouts.LEARN_ROOM, Terrain.GROUND_A) == 8, "导师标记数量错误");
		check(count(LearnRoomLayouts.LEARN_ROOM, Terrain.EMPTY_SP) == 2, "稻草人标记数量错误");
		check(count(LearnRoomLayouts.LEARN_ROOM, Terrain.SECRET_TRAP) == 2, "陷阱标记数量错误");
	}

	private static void testHiddenBeginnerClass() {
		check(GamesInProgress.MAX_SLOTS == HeroClass.playableClasses().length,
				"隐藏职业改变了普通存档槽位数量");
		check(GamesInProgress.MAX_SLOTS == 9, "SPS普通存档槽位数量应与九个可见职业一致（含决斗家）");
		for (HeroClass heroClass : HeroClass.playableClasses())
			check(heroClass != HeroClass.NEWPLAYER, "隐藏新手职业出现在职业选择中");
		Hero hero = new Hero();
		HeroClass.NEWPLAYER.initHero(hero);
		check(hero.heroClass == HeroClass.NEWPLAYER, "教学职业初始化错误");
		check(hero.HT == 50 && hero.HP == 10, "教学职业生命值应为10/50");
		check(hero.belongings.backpack.items.isEmpty(), "教学职业不应携带普通开局物品");
		check(hero.belongings.weapon() == null && hero.belongings.armor() == null,
				"教学职业不应携带武器或护甲");
	}

	private static void testLearnLevelContent() {
		prepareTutorial();
		LearnLevel level = new LearnLevel();
		Dungeon.level = level;
		level.create();
		check(level.width() == 48 && level.height() == 48, "教学层尺寸错误");
		check(level.entrance() == 147 && level.exit() == 2256, "教学层旧版入口出口坐标错误");
		check(Arrays.hashCode(level.map) == 431741786, "教学层构建后改变了固定地图");
		check(level.customTiles.size() == 1 && level.customTiles.get(0) instanceof SpsLegacyLevelVisual,
				"教学层旧版拼图图层缺失");

		int guides = 0, scarecrows = 0;
		for (Mob mob : level.mobs()) {
			if (mob instanceof Leadercn) guides++;
			if (mob instanceof TestMob) {
				scarecrows++;
				check(mob.HP == 100 && mob.HT == 100000, "教学稻草人数值错误");
			}
		}
		check(guides == 8 && scarecrows == 2 && level.mobs().size() == 10, "教学怪物/NPC数量错误");
		check(level.traps.valueList().size() == 2, "教学火焰陷阱数量错误");
		for (pd.levels.traps.Trap trap : level.traps.valueList())
			check(trap instanceof FireBuffTrap, "教学层生成了错误陷阱");

		check(level.heaps.valueList().size() == 13, "教学固定物品堆数量错误");
		checkHeap(level, 15 + 48 * 2, Heap.Type.HEAP, TestWeapon.class, 1);
		checkHeap(level, 16 + 48 * 2, Heap.Type.HEAP, TestArmor.class, 1);
		checkHeap(level, 40 + 48 * 3, Heap.Type.CHEST, PotionOfMending.class, 1);
		checkHeap(level, 41 + 48 * 3, Heap.Type.E_DUST, IronKey.class, 1);
		checkHeap(level, 42 + 48 * 4, Heap.Type.E_DUST, VioletDewdrop.class, 1);
		checkHeap(level, 42 + 48 * 5, Heap.Type.CHEST, Gold.class, 1000);
		checkHeap(level, 37 + 48 * 35, Heap.Type.M_WEB, SmallMeat.class, 1);
		checkHeap(level, 38 + 48 * 34, Heap.Type.M_WEB, SmallMeat.class, 1);
		checkHeap(level, 38 + 48 * 36, Heap.Type.M_WEB, SmallMeat.class, 1);
		checkHeap(level, 39 + 48 * 35, Heap.Type.M_WEB, SmallMeat.class, 1);
		checkHeap(level, 14 + 48 * 18, Heap.Type.FOR_SALE, ScrollOfMagicMapping.class, 1);
		checkHeap(level, 15 + 48 * 18, Heap.Type.FOR_LIFE, PotionOfMindVision.class, 1);
		checkHeap(level, 7 + 48 * 39, Heap.Type.HEAP, KnowledgeBook.class, 1);
	}

	private static void testUnusedStartLevel() {
		prepareTutorial();
		StartLevel level = new StartLevel();
		Dungeon.level = level;
		level.create();
		int tinkerers = 0;
		for (Mob mob : level.mobs()) if (mob instanceof Tinkerer1) tinkerers++;
		check(Arrays.hashCode(level.map) == 431741786, "旧版起始层地图错误");
		check(tinkerers == 8 && level.mobs().size() == 8, "旧版起始层NPC数量错误");
		check(level.heaps.valueList().isEmpty() && level.traps.valueList().size() == 2,
				"旧版起始层物品或陷阱错误");
	}

	private static void testGuideRewardsAndPersistence() {
		prepareTutorial();
		Class<?>[] expected = {IronKey.class, null, null, null, StoneOre.class,
				WandOfTest.class, DungeonBomb.class, PuddingCup.class};
		for (int lesson = 0; lesson < expected.length; lesson++) {
			Object reward = Leadercn.rewardForLesson(lesson);
			check(expected[lesson] == null ? reward == null : expected[lesson].isInstance(reward),
					"导师第" + (lesson + 1) + "段奖励错误");
		}
		Bundle bundle = new Bundle();
		new Leadercn().storeInBundle(bundle);
		bundle.put("lesson", 6);
		Leadercn restored = new Leadercn();
		restored.restoreFromBundle(bundle);
		check(restored.lessonForTesting() == 6, "导师进度存档往返错误");
		check(Dungeon.LEARN_BRANCH == -1, "教学路线必须使用独立分支");
		GamesInProgress.curSlot = 0;
		try {
			Dungeon.saveAll();
		} catch (IOException exception) {
			throw new AssertionError("教学路线不应写入普通存档", exception);
		}
	}

	private static void prepareTutorial() {
		Dungeon.seed = 0x5350534C4541524EL;
		Dungeon.depth = 1;
		Dungeon.branch = Dungeon.LEARN_BRANCH;
		Dungeon.hero = new Hero();
		HeroClass.NEWPLAYER.initHero(Dungeon.hero);
	}

	private static void checkHeap(Level level, int cell, Heap.Type type, Class<?> itemClass, int quantity) {
		Heap heap = level.heaps.get(cell);
		check(heap != null && heap.type == type, "固定物品堆类型错误，坐标=" + cell);
		check(heap.peek() != null && itemClass.isInstance(heap.peek()), "固定物品类型错误，坐标=" + cell);
		check(heap.peek().quantity() == quantity, "固定物品数量错误，坐标=" + cell);
	}

	private static int count(int[] values, int target) {
		int count = 0;
		for (int value : values) if (value == target) count++;
		return count;
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}
}
