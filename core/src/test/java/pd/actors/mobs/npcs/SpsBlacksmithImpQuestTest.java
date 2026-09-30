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
import pd.actors.hero.Hero;
import pd.actors.mobs.Golem;
import pd.items.Generator;
import pd.items.Heap;
import pd.items.Item;
import pd.items.armor.LeatherArmor;
import pd.items.rings.Ring;
import pd.items.rings.RingOfAccuracy;
import pd.items.weapon.melee.Mace;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.traps.Trap;
import pd.plants.Plant;
import render.noosa.Game;
import render.utils.Bundle;
import render.utils.FileUtils;
import render.utils.Random;
import render.utils.SparseArray;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;

/** Runtime parity checks for the SPS-PD 0.9.8 blacksmith and imp quests. */
public final class SpsBlacksmithImpQuestTest {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		FileUtils.setDefaultFileProperties(Files.FileType.Absolute,
				System.getProperty("java.io.tmpdir") + "sps-smith-imp-quest" + File.separator);
		Game.version = "test";
		Random.pushGenerator(0x535053534D495448L);
		try {
			Generator.fullReset();
			Ring.initGems();
			testBlacksmithRulesAndMigration();
			testImpMigrationAndReward();
			testImpBossFloorDrops();
			testLegacyLabels();
			System.out.println("SPS铁匠与小恶魔任务通过：15枚暗金、单次重铸、6/8枚令牌、+2诅咒戒指、20层掉落、存档迁移和四语文本均符合0.9.8。");
		} finally {
			Random.popGenerator();
			Blacksmith.Quest.reset();
			Imp.Quest.reset();
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			app.exit();
		}
	}

	private static void testBlacksmithRulesAndMigration() {
		Item first = new Mace().identify(false).level(0);
		Item material = new LeatherArmor().identify(false).level(2);
		check(Blacksmith.verifyLegacy(first, material) == null,
				"旧铁匠错误要求两件装备类型相同");
		material.level(0);
		check(Blacksmith.verifyLegacy(first, material) != null,
				"旧铁匠接受了未强化的素材装备");

		Bundle modern = blacksmithBundle(false, true, false, false);
		Blacksmith.Quest.restoreFromBundle(modern);
		Bundle migrated = new Bundle();
		Blacksmith.Quest.storeInBundle(migrated);
		Bundle node = migrated.getBundle("blacksmith");
		check(node.getBoolean("old_quest") && !node.getBoolean("given")
				&& !node.getBoolean("completed"), "未完成的破碎铁匠任务没有迁移回旧暗金任务");

		Blacksmith.Quest.restoreFromBundle(blacksmithBundle(false, true, true, false));
		check(Blacksmith.Quest.isLegacy() && Blacksmith.Quest.completed()
				&& Blacksmith.Quest.rewardsAvailable(), "已完成的破碎铁匠任务没有保留一次旧重铸奖励");

		Bundle original = new Bundle();
		Bundle originalNode = new Bundle();
		originalNode.put("spawned", true);
		originalNode.put("given", true);
		originalNode.put("completed", true);
		originalNode.put("reforged", false);
		original.put("blacksmith", originalNode);
		Blacksmith.Quest.restoreFromBundle(original);
		check(Blacksmith.Quest.isLegacy() && Blacksmith.Quest.rewardsAvailable(),
				"0.9.8铁匠存档没有恢复单次重铸奖励");
	}

	private static void testImpMigrationAndReward() {
		Imp.Quest.restoreFromBundle(impBundle(false, false, true, false, null));
		Bundle migrated = new Bundle();
		Imp.Quest.storeInBundle(migrated);
		Bundle node = migrated.getBundle("demon");
		Item reward = (Item)node.get("reward");
		check(node.getBoolean("old_quest") && !node.getBoolean("given")
				&& reward instanceof Ring && reward.level() == 2 && reward.cursed,
				"未完成的破碎小恶魔任务没有迁移为带+2诅咒戒指的旧任务");

		Imp.Quest.restoreFromBundle(impBundle(false, false, true, true, null));
		check(Imp.Quest.isOld() && Imp.Quest.isCompleted() && Imp.Quest.earnedShop(),
				"已完成的破碎小恶魔任务没有保留旧商店解锁");

		Imp.Quest.restoreFromBundle(impBundle(true, true, false, false, new RingOfAccuracy()));
		check(Imp.Quest.legacyTokenGoal() == 8, "武僧任务令牌需求不是8枚");
		Imp.Quest.restoreFromBundle(impBundle(true, false, false, false, new RingOfAccuracy()));
		check(Imp.Quest.legacyTokenGoal() == 6, "魔像任务令牌需求不是6枚");
	}

	private static void testImpBossFloorDrops() {
		Actor.clear();
		Dungeon.depth = 20;
		Dungeon.hero = new Hero();
		Dungeon.hero.pos = 100;
		TestLevel level = new TestLevel();
		Dungeon.level = level;
		Imp.Quest.restoreFromBundle(impBundle(true, false, true, false, new RingOfAccuracy()));
		Golem golem = new Golem();
		golem.pos = 200;
		Imp.Quest.oldProcess(golem);
		check(level.heaps.get(200) != null
				&& level.heaps.get(200).peek() instanceof pd.items.quest.DwarfToken,
				"20层魔像击杀没有按0.9.8掉落任务令牌");
	}

	private static void testLegacyLabels() throws Exception {
		String[] files = {"en/actors.properties", "zh/actors.properties",
				"zh-hant/actors.properties", "ru/actors.properties"};
		for (String file : files) {
			Properties actors = load("messages/actors/" + file);
			String golems = required(actors, "actors.mobs.npcs.imp.old_golems_1", file);
			String monks = required(actors, "actors.mobs.npcs.imp.old_monks_1", file);
			check(golems.contains("6") && monks.contains("8"),
					"小恶魔任务文本没有写明6/8枚令牌：" + file);
			for (String key : new String[]{"gold_1", "gold_2", "keeppickaxe", "completed",
					"same_item", "un_ided", "cursed", "degraded", "need_reinforced", "cant_reforge"}) {
				required(actors, "actors.mobs.npcs.blacksmith." + key, file);
			}
		}

		String[] windowFiles = {"en/windows.properties", "zh/windows.properties",
				"zh-hant/windows.properties", "ru/windows.properties"};
		for (String file : windowFiles) {
			Properties windows = load("messages/windows/" + file);
			for (String key : new String[]{"prompt", "select1", "select2", "reforge"}) {
				required(windows, "windows.wndblacksmithlegacy." + key, file);
			}
			required(windows, "windows.wndimpold.message", file);
			required(windows, "windows.wndimpold.reward", file);
		}
	}

	private static Bundle blacksmithBundle(boolean oldQuest, boolean given,
			boolean completed, boolean reforged) {
		Bundle root = new Bundle();
		Bundle node = new Bundle();
		node.put("spawned", true);
		node.put("old_quest", oldQuest);
		node.put("given", given);
		node.put("completed", completed);
		node.put("reforged", reforged);
		root.put("blacksmith", node);
		return root;
	}

	private static Bundle impBundle(boolean oldQuest, boolean alternative,
			boolean given, boolean completed, Item reward) {
		Bundle root = new Bundle();
		Bundle node = new Bundle();
		node.put("spawned", true);
		node.put("old_quest", oldQuest);
		node.put("alternative", alternative);
		node.put("given", given);
		node.put("completed", completed);
		if (reward != null) node.put("reward", reward);
		node.put("reward_options", new ArrayList<Item>());
		root.put("demon", node);
		return root;
	}

	private static Properties load(String path) throws Exception {
		Properties properties = new Properties();
		try (InputStreamReader reader = new InputStreamReader(
				java.nio.file.Files.newInputStream(Path.of(path)), StandardCharsets.UTF_8)) {
			properties.load(reader);
		}
		return properties;
	}

	private static String required(Properties properties, String key, String file) {
		String value = properties.getProperty(key);
		check(value != null && !value.isEmpty(), "缺少任务文本 " + key + "：" + file);
		return value;
	}

	private static final class TestLevel extends Level {
		TestLevel() {
			setSize(32, 32);
			mobs = new HashSet<>(); heaps = new SparseArray<Heap>(); blobs = new HashMap<>();
			plants = new SparseArray<Plant>(); traps = new SparseArray<Trap>(); transitions = new ArrayList<>();
			customTiles = new ArrayList<>(); customTerrain = new ArrayList<>(); customWalls = new ArrayList<>();
			Arrays.fill(map, Terrain.EMPTY); Arrays.fill(passable, true); buildFlagMaps();
		}
		@Override protected boolean build() { return true; }
		@Override protected void createMobs() { }
		@Override protected void createItems() { }
		@Override public String tilesTex() { return null; }
		@Override public String waterTex() { return null; }
		@Override public Heap drop(Item item, int cell) {
			Heap heap = heaps.get(cell);
			if (heap == null) {
				heap = new Heap();
				heap.pos = cell;
				heaps.put(cell, heap);
			}
			heap.drop(item);
			return heap;
		}
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsBlacksmithImpQuestTest() { }
}
