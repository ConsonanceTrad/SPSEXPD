package pd.actors.mobs.npcs;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.items.AdamantWand;
import pd.items.Heap;
import pd.items.quest.CorpseDust;
import pd.items.equipment.wands.Wand;
import pd.items.equipment.wands.WandOfTCloud;
import pd.items.equipment.wands.fusion.WandOfFlow;
import pd.journal.Notes;
import pd.levels.PrisonLevel;
import pd.levels.Terrain;
import pd.levels.rooms.standard.EmptyRoom;
import pd.levels.traps.Trap;
import pd.plants.Plant;
import pd.plants.Rotberry;
import render.noosa.Game;
import render.utils.data.SparseArray;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import render.utils.serialize.FileUtils;

import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;

/** Runtime parity checks for the SPS-PD 0.9.8 wandmaker quest. */
public final class SpsWandmakerQuestTest {

	private static final String[] BATTLE = {
			"WandOfLight", "WandOfDisintegration", "WandOfFirebolt", "WandOfLightning",
			"WandOfAcid", "WandOfBlood", "WandOfFreeze"
	};
	private static final String[] UTILITY = {
			"WandOfCharm", "WandOfFlock", "WandOfSwamp", "WandOfMeteorite",
			"WandOfFlow", "WandOfTCloud", "WandOfFlow"
	};

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		FileUtils.setDefaultFileProperties(Files.FileType.Absolute,
				System.getProperty("java.io.tmpdir") + "sps-wandmaker-quest" + File.separator);
		Game.version = "test";
		Random.pushGenerator(0x53505357414E444DL);
		try {
			Notes.reset();
			testWandPools();
			testDepthSevenSpawnAndPersistence();
			testQuestItemPlacementAndBoundedFailure();
			testPrePortSaveMigration();
			testLegacyLabels();
			System.out.println("SPS法杖匠任务通过：第7层生成、尸尘/腐莓、两组法杖、精金奖励、有限落点、存档迁移和四语文本均符合0.9.8。");
		} finally {
			Random.popGenerator();
			Wandmaker.Quest.reset();
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			app.exit();
		}
	}

	private static void testWandPools() throws Exception {
		Method battle = Wandmaker.Quest.class.getDeclaredMethod("battleWand", int.class);
		Method utility = Wandmaker.Quest.class.getDeclaredMethod("utilityWand", int.class);
		battle.setAccessible(true);
		utility.setAccessible(true);
		for (int i = 0; i < 7; i++) {
			check(((Wand) battle.invoke(null, i)).getClass().getSimpleName().equals(BATTLE[i]),
					"战斗法杖池错误：" + i);
			check(((Wand) utility.invoke(null, i)).getClass().getSimpleName().equals(UTILITY[i]),
					"辅助法杖池错误：" + i);
		}
		check(utility.invoke(null, 4) instanceof WandOfFlow
				&& utility.invoke(null, 5) instanceof WandOfTCloud
				&& utility.invoke(null, 6) instanceof WandOfFlow,
				"辅助法杖池未保留源代码的流动法杖双权重");
		check(Wandmaker.Quest.completionBonus() instanceof AdamantWand,
				"法杖匠任务没有额外奖励精金法杖");
	}

	private static void testDepthSevenSpawnAndPersistence() {
		Wandmaker.Quest.reset();
		TestPrisonLevel level = setupLevel();
		EmptyRoom room = new EmptyRoom();
		room.set(2, 2, 10, 10);
		Dungeon.depth = 6;
		Wandmaker.Quest.spawn(level, room);
		check(level.mobs().isEmpty(), "法杖匠在第7层之外生成");

		Dungeon.depth = 7;
		Wandmaker.Quest.spawn(level, room);
		check(level.mobs().size() == 1 && level.mobs().iterator().next() instanceof Wandmaker,
				"法杖匠没有在第7层入口房生成");
		check(contains(BATTLE, Wandmaker.Quest.wand1.getClass().getSimpleName())
				&& contains(UTILITY, Wandmaker.Quest.wand2.getClass().getSimpleName()),
				"第7层法杖匠奖励不在旧版双池中");

		Bundle stored = new Bundle();
		Wandmaker.Quest.storeInBundle(stored);
		Wand first = Wandmaker.Quest.wand1;
		Wand second = Wandmaker.Quest.wand2;
		Wandmaker.Quest.reset();
		Wandmaker.Quest.restoreFromBundle(stored);
		check(Wandmaker.Quest.wand1.getClass() == first.getClass()
				&& Wandmaker.Quest.wand2.getClass() == second.getClass(), "法杖匠奖励读档后改变类型");
	}

	private static void testQuestItemPlacementAndBoundedFailure() {
		TestPrisonLevel level = setupLevel();
		Heap skeleton = new Heap();
		skeleton.pos = 200;
		skeleton.type = Heap.Type.SKELETON;
		level.heaps.put(skeleton.pos, skeleton);
		Wandmaker.Quest.restoreFromBundle(questBundle(true, false));
		Wandmaker.Quest.placeItem();
		check(skeleton.peek() instanceof CorpseDust && storedGiven(), "尸尘没有放入不可见骨堆");

		level = setupLevel();
		Wandmaker.Quest.restoreFromBundle(questBundle(false, false));
		Wandmaker.Quest.placeItem();
		check(level.plants.get(level.respawnCell) instanceof Rotberry && storedGiven(),
				"腐莓种子没有种在合法空格");

		level = setupLevel();
		Heap blocked = new Heap();
		blocked.pos = level.respawnCell;
		level.heaps.put(blocked.pos, blocked);
		Wandmaker.Quest.restoreFromBundle(questBundle(false, false));
		Wandmaker.Quest.placeItem();
		check(!storedGiven() && level.plants.get(level.respawnCell) == null,
				"无空位时法杖匠任务未有限退出");
	}

	private static void testPrePortSaveMigration() {
		Bundle root = new Bundle();
		Bundle node = new Bundle();
		node.put("spawned", true);
		node.put("type", 2);
		node.put("given", true);
		node.put("wand1", new pd.items.equipment.wands.WandOfFirebolt());
		node.put("wand2", new WandOfFlow());
		root.put("wandmaker", node);
		Wandmaker.Quest.restoreFromBundle(root);
		Bundle stored = new Bundle();
		Wandmaker.Quest.storeInBundle(stored);
		Bundle migrated = stored.getBundle("wandmaker");
		check(migrated.getBoolean("alternative") && !migrated.getBoolean("given"),
				"破碎版余烬任务存档未迁移为可重新派发的尸尘任务");
	}

	private static void testLegacyLabels() throws Exception {
		String[][] expected = {
				{"en/windows.properties", "As I promised, you can choose one of my high quality wands.", "Battle Wand", "Non-Battle Wand"},
				{"zh/windows.properties", "哦，你成功了，希望没给你带来太多麻烦。选择你的奖励吧。", "战斗法杖", "辅助法杖"},
				{"zh-hant/windows.properties", "哦，你成功了，希望沒給你帶來太多麻煩。選擇你的獎勵吧。", "戰鬥法杖", "輔助法杖"},
				{"ru/windows.properties", "Как и обещал, ты можешь выбрать одну из моих лучших палочек.", "Боевая палочка", "Небоевая палочка"}
		};
		for (String[] row : expected) {
			Properties properties = new Properties();
			Path path = Path.of("messages", "windows", row[0]);
			try (InputStreamReader reader = new InputStreamReader(java.nio.file.Files.newInputStream(path), StandardCharsets.UTF_8)) {
				properties.load(reader);
			}
			check(row[1].equals(properties.getProperty("windows.wndwandmaker.message"))
					&& row[2].equals(properties.getProperty("windows.wndwandmaker.battle"))
					&& row[3].equals(properties.getProperty("windows.wndwandmaker.no_battle")),
					"法杖匠窗口文本不匹配0.9.8：" + row[0]);
		}
	}

	private static boolean storedGiven() {
		Bundle root = new Bundle();
		Wandmaker.Quest.storeInBundle(root);
		return root.getBundle("wandmaker").getBoolean("given");
	}

	private static Bundle questBundle(boolean alternative, boolean given) {
		Bundle root = new Bundle();
		Bundle node = new Bundle();
		node.put("spawned", true);
		node.put("alternative", alternative);
		node.put("given", given);
		node.put("wand1", new pd.items.equipment.wands.WandOfLight());
		node.put("wand2", new WandOfFlow());
		root.put("wandmaker", node);
		return root;
	}

	private static TestPrisonLevel setupLevel() {
		Actor.clear();
		Dungeon.depth = 7;
		Dungeon.hero = new Hero();
		Dungeon.hero.pos = 100;
		Dungeon.hero.HP = Dungeon.hero.HT = 100;
		TestPrisonLevel level = new TestPrisonLevel();
		Dungeon.level = level;
		return level;
	}

	private static boolean contains(String[] values, String value) {
		return Arrays.asList(values).contains(value);
	}

	private static final class TestPrisonLevel extends PrisonLevel {
		final int respawnCell = 300;
		TestPrisonLevel() {
			setSize(32, 32);
			mobs().clear(); heaps = new SparseArray<Heap>(); blobs = new HashMap<>();
			plants = new SparseArray<Plant>(); traps = new SparseArray<Trap>(); transitions = new ArrayList<>();
			customTiles = new ArrayList<>(); customTerrain = new ArrayList<>(); customWalls = new ArrayList<>();
			Arrays.fill(map, Terrain.EMPTY); Arrays.fill(passable, true); buildFlagMaps();
		}
		@Override public int randomRespawnCell(Char ch) { return respawnCell; }
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsWandmakerQuestTest() { }
}
