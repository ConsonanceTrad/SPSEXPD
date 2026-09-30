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
import pd.items.Generator;
import pd.items.Heap;
import pd.items.armor.normalarmor.ClothArmor;
import pd.items.artifacts.Artifact;
import pd.items.eggs.Egg;
import pd.items.rings.Ring;
import pd.items.weapon.melee.normalweapon.Club;
import pd.journal.Notes;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.traps.Trap;
import pd.plants.Plant;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.FileUtils;
import com.watabou.utils.Random;
import com.watabou.utils.SparseArray;

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

/** Runtime parity checks for the SPS-PD 0.9.8 sad ghost quest. */
public final class SpsGhostQuestTest {

	private static final String[] ARTIFACTS = {
			"CapeOfThorns", "ChaliceOfBlood", "CloakOfShadows", "HornOfPlenty",
			"MasterThievesArmband", "SandalsOfNature", "TalismanOfForesight",
			"TimekeepersHourglass", "UnstableSpellbook", "AlchemistsToolkit",
			"RobotDMT", "EyeOfSkadi", "EtherealChains", "DriedRose", "GlassTotem",
			"AlienBag", "FlyChains", "TimeOclock"
	};

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		FileUtils.setDefaultFileProperties(Files.FileType.Absolute,
				System.getProperty("java.io.tmpdir") + "sps-ghost-quest" + File.separator);
		Game.version = "test";
		Random.pushGenerator(0x53505347484F5354L);
		try {
			Generator.fullReset();
			Ring.initGems();
			Notes.reset();
			testLegacyArtifactPool();
			testRewardGenerationAndPersistence();
			testKillProgressAndCompletion();
			testPrePortSaveMigration();
			testLegacyLabels();
			System.out.println("SPS幽灵任务通过：三类首领、神器/戒指/宠物蛋奖励、击杀推进、存档迁移和四语文本均符合0.9.8。");
		} finally {
			Random.popGenerator();
			Ghost.Quest.reset();
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			app.exit();
		}
	}

	private static void testLegacyArtifactPool() {
		check(Generator.Category.ARTIFACT.classes.length == ARTIFACTS.length,
				"普通神器池数量不是0.9.8的18项");
		for (int i = 0; i < ARTIFACTS.length; i++) {
			check(Generator.Category.ARTIFACT.classes[i].getSimpleName().equals(ARTIFACTS[i]),
					"普通神器池顺序错误：" + i);
			float expected = i == 2 || i == 16 || i == 17 ? 0f : 1f;
			check(Generator.Category.ARTIFACT.defaultProbs[i] == expected,
					"普通神器池权重错误：" + ARTIFACTS[i]);
		}
	}

	private static void testRewardGenerationAndPersistence() throws Exception {
		Ghost.Quest.reset();
		Method generate = Ghost.Quest.class.getDeclaredMethod("generateRewards");
		generate.setAccessible(true);
		generate.invoke(null);
		check(Ghost.Quest.artifact instanceof Artifact && !Ghost.Quest.artifact.cursed
				&& Ghost.Quest.artifact.isIdentified(), "幽灵神器奖励无效、被诅咒或未鉴定");
		check(Ghost.Quest.ring instanceof Ring && !Ghost.Quest.ring.cursed
				&& Ghost.Quest.ring.isIdentified(), "幽灵戒指奖励无效、被诅咒或未鉴定");
		check(Ghost.Quest.pet instanceof Egg, "幽灵没有生成基础宠物蛋奖励");

		Bundle input = questBundle(3, true, false, Ghost.Quest.artifact, Ghost.Quest.ring, Ghost.Quest.pet);
		Ghost.Quest.restoreFromBundle(input);
		Bundle output = new Bundle();
		Ghost.Quest.storeInBundle(output);
		Bundle node = output.getBundle("sadGhost");
		check(node.get("artifact") instanceof Artifact && node.get("ring") instanceof Ring
				&& node.get("pet") instanceof Egg, "幽灵三项奖励未完整写入存档");
	}

	private static void testKillProgressAndCompletion() {
		TestLevel level = setupLevel();
		Ghost ghost = new Ghost();
		ghost.pos = 200;
		level.mobs.add(ghost);
		Ghost.Quest.restoreFromBundle(questBundle(3, true, false,
				Ghost.Quest.artifact, Ghost.Quest.ring, Ghost.Quest.pet));
		Ghost.Quest.process();
		Bundle output = new Bundle();
		Ghost.Quest.storeInBundle(output);
		check(output.getBundle("sadGhost").getBoolean("processed"), "任务首领死亡后幽灵任务未推进");
		check(Ghost.Quest.processed() && !Ghost.Quest.completed(), "领取奖励前任务完成状态错误");
		Ghost.Quest.complete();
		check(Ghost.Quest.completed() && Ghost.Quest.artifact == null && Ghost.Quest.ring == null
				&& Ghost.Quest.pet == null, "领取奖励后任务未完成或奖励未清空");
	}

	private static void testPrePortSaveMigration() throws Exception {
		Bundle root = new Bundle();
		Bundle node = new Bundle();
		node.put("spawned", true);
		node.put("type", 1);
		node.put("given", true);
		node.put("processed", true);
		node.put("depth", 2);
		node.put("weapon", new Club());
		node.put("armor", new ClothArmor());
		root.put("sadGhost", node);
		Ghost.Quest.restoreFromBundle(root);
		Method migrate = Ghost.Quest.class.getDeclaredMethod("ensureLegacyRewards");
		migrate.setAccessible(true);
		migrate.invoke(null);
		check(Ghost.Quest.artifact != null && Ghost.Quest.ring != null && Ghost.Quest.pet != null,
				"移植前幽灵任务存档未迁移为SPS三选一奖励");
	}

	private static void testLegacyLabels() throws Exception {
		String[][] expected = {
				{"windows.properties", "Ghost's Artifact", "Ghost's Ring", "Ghost's Pet"},
				{"windows_zh.properties", "幽灵的饰品", "幽灵的信物", "幽灵的玩伴"},
				{"windows_zh-hant.properties", "幽靈的飾品", "幽靈的信物", "幽靈的玩伴"},
				{"windows_ru.properties", "Оружие призрака", "Доспех призрака", "Питомец призрака"}
		};
		for (String[] row : expected) {
			Properties properties = new Properties();
			Path path = Path.of("messages", "windows", row[0]);
			try (InputStreamReader reader = new InputStreamReader(java.nio.file.Files.newInputStream(path), StandardCharsets.UTF_8)) {
				properties.load(reader);
			}
			check(row[1].equals(properties.getProperty("windows.wndsadghost.weapon"))
					&& row[2].equals(properties.getProperty("windows.wndsadghost.armor"))
					&& row[3].equals(properties.getProperty("windows.wndsadghost.pet")),
					"幽灵奖励按钮文本不匹配0.9.8：" + row[0]);
		}
	}

	private static Bundle questBundle(int depth, boolean given, boolean processed,
			Artifact artifact, Ring ring, Egg pet) {
		Bundle root = new Bundle();
		Bundle node = new Bundle();
		node.put("spawned", true);
		node.put("type", depth - 1);
		node.put("given", given);
		node.put("processed", processed);
		node.put("depth", depth);
		node.put("artifact", artifact);
		node.put("ring", ring);
		node.put("pet", pet);
		root.put("sadGhost", node);
		return root;
	}

	private static TestLevel setupLevel() {
		Actor.clear();
		Dungeon.depth = 3;
		Dungeon.hero = new Hero();
		Dungeon.hero.pos = 100;
		Dungeon.hero.HP = Dungeon.hero.HT = 100;
		TestLevel level = new TestLevel();
		Dungeon.level = level;
		return level;
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
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsGhostQuestTest() { }
}
