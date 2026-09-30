package pd.items.quest;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.items.ChallengeBook;
import pd.items.Heap;
import pd.items.Item;
import pd.items.challengelists.CaveChallenge;
import pd.items.challengelists.ChallengeList;
import pd.items.challengelists.ChallengePageDrops;
import pd.items.challengelists.CityChallenge;
import pd.items.challengelists.CourageChallenge;
import pd.items.challengelists.IceChallenge;
import pd.items.challengelists.PowerChallenge;
import pd.items.challengelists.PrisonChallenge;
import pd.items.challengelists.SewerChallenge;
import pd.items.challengelists.WisdomChallenge;
import pd.levels.Level;
import pd.plants.Plant;
import render.noosa.Game;
import render.utils.data.SparseArray;
import render.utils.serialize.Bundle;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/** Verifies the legacy page-binding progression and its save contract. */
public final class SpsChallengeJournalTest {

	private SpsChallengeJournalTest() { }

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Dungeon.branch = 0;
		Dungeon.hero = new Hero();
		Dungeon.level = new RecordingLevel();

		ChallengeList[] pages = {
				new SewerChallenge(), new PrisonChallenge(), new CaveChallenge(),
				new CityChallenge(), new IceChallenge(), new CourageChallenge(),
				new PowerChallenge(), new WisdomChallenge()
		};
		int[] depths = {4, 9, 14, 19, 24, 9, 19, 24};
		ChallengeJournal journal = ChallengeJournal.ensureFor(Dungeon.hero);
		check(Dungeon.hero.belongings.getItem(ChallengeJournal.class) == journal,
				"挑战日志未加入背包");
		check(journal instanceof ChallengeBook, "新取得的挑战日志不是旧版ChallengeBook实体");
		for (int i = 0; i < pages.length; i++) {
			check(pages[i].challenge() == i, pages[i].getClass().getSimpleName() + "纸片编号错误");
			check(ChallengeJournal.anchorDepth(i) == depths[i], "挑战" + i + "锚点层数错误");
			check(ChallengeJournal.challengeForBranch(ChallengeJournal.branchFor(i)) == i,
					"挑战" + i + "支线映射不可逆");
			check(!journal.isUnlocked(i), "挑战" + i + "在装订前已解锁");
			check(journal.addPage(pages[i]), "挑战" + i + "首次装订失败");
			check(journal.isUnlocked(i), "挑战" + i + "装订后未解锁");
			check(!journal.addPage(pages[i]), "挑战" + i + "允许重复装订");
		}
		check("8/8".equals(journal.status()), "挑战日志记录数量错误：" + journal.status());

		Bundle saved = new Bundle();
		journal.storeInBundle(saved);
		ChallengeJournal restored = new ChallengeJournal();
		restored.restoreFromBundle(saved);
		for (int i = 0; i < pages.length; i++) {
			check(restored.isUnlocked(i), "挑战" + i + "存档恢复后丢失");
		}

		Dungeon.hero = new Hero();
		RecordingLevel level = new RecordingLevel();
		Dungeon.level = level;
		ChallengeJournal dropJournal = ChallengeJournal.ensureFor(Dungeon.hero);
		ChallengePageDrops.offer(new SewerChallenge(), 10);
		ChallengePageDrops.offer(new SewerChallenge(), 11);
		check(countPages(level, SewerChallenge.class) == 1, "同一挑战纸片重复掉落");
		check(dropJournal.addPage(new SewerChallenge()), "掉落防重测试装订失败");
		level.heaps.clear();
		ChallengePageDrops.offer(new SewerChallenge(), 12);
		check(countPages(level, SewerChallenge.class) == 0, "已装订纸片仍会掉落");

		ChallengeJournal legacy = new ChallengeJournal();
		legacy.migrateLegacyRegions(14);
		check(legacy.isUnlocked(0) && legacy.isUnlocked(1) && legacy.isUnlocked(2)
				&& !legacy.isUnlocked(3), "早期移植存档迁移范围错误");
		legacy.migrateLegacyRegions(24);
		check(!legacy.isUnlocked(3) && !legacy.isUnlocked(4), "旧存档迁移被重复执行");

		Dungeon.hero = new Hero();
		MapFragment fragment = new MapFragment().forChallenge(0);
		Dungeon.hero.belongings.backpack.items.add(fragment);
		fragment.execute(Dungeon.hero, MapFragment.AC_ADD);
		check(Dungeon.hero.belongings.getItem(ChallengeJournal.class) == null
				&& Dungeon.hero.belongings.getItem(MapFragment.class) == fragment,
				"没有REN挑战日志时，地图碎片仍凭空创建日志或被消耗");

		Path java = Path.of("..", "java", "pd");
		String heroClass = Files.readString(java.resolve(Path.of("actors", "hero", "HeroClass.java")), StandardCharsets.UTF_8);
		String dungeon = Files.readString(java.resolve("Dungeon.java"), StandardCharsets.UTF_8);
		String townNpc = Files.readString(java.resolve(Path.of("actors", "mobs", "npcs", "TownNpc.java")), StandardCharsets.UTF_8);
		check(!heroClass.contains("new ChallengeJournal().collect()"), "普通职业仍在开局免费获得挑战日志");
		check(!dungeon.contains("ChallengeJournal.ensureFor(hero)"), "读档仍会无条件凭空创建挑战日志");
		check(townNpc.contains("dropAtHero(new ChallengeBook())"), "REN首次交谈没有掉落实体ChallengeBook");
		String en = Files.readString(Path.of("messages", "items", "en", "items.properties"), StandardCharsets.UTF_8);
		String zh = Files.readString(Path.of("messages", "items", "zh", "items.properties"), StandardCharsets.UTF_8);
		check(en.contains("items.challengebook.name=") && zh.contains("items.challengebook.name=")
				&& en.indexOf('\uFFFD') < 0 && zh.indexOf('\uFFFD') < 0,
				"ChallengeBook双语资源缺失或含乱码");

		System.out.println("SPS挑战日志测试通过：REN实体来源、八张纸片编号、装订去重、地点解锁、缺书保护、掉落防重及存档迁移均正常。");
		app.exit();
	}

	private static int countPages(Level level, Class<? extends ChallengeList> type) {
		int result = 0;
		for (Heap heap : level.heaps.values()) for (Item item : heap.items) {
			if (type.isInstance(item)) result += item.quantity();
		}
		return result;
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private static final class RecordingLevel extends Level {
		RecordingLevel() {
			setSize(16, 16);
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
}
