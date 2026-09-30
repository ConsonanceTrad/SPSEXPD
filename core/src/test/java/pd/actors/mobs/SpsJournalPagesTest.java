package pd.actors.mobs;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.QuickSlot;
import pd.Statistics;
import pd.actors.hero.Hero;
import pd.items.Heap;
import pd.items.Item;
import pd.items.DolyaSlate;
import pd.items.bags.ScrollHolder;
import pd.items.journalpages.JournalPage;
import pd.items.journalpages.NewHome;
import pd.items.journalpages.SafeSpotPage;
import pd.items.journalpages.Sokoban1;
import pd.items.journalpages.Sokoban2;
import pd.items.journalpages.Sokoban3;
import pd.items.journalpages.Sokoban4;
import pd.items.journalpages.Town;
import pd.items.misc.LuckyBadge;
import pd.items.quest.AdventureJournal;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.rooms.special.ShopRoom;
import pd.levels.rooms.special.SpsShopRoom;
import pd.levels.traps.Trap;
import pd.plants.Plant;
import pd.sprites.ItemSprite;
import pd.sprites.ItemSpriteSheet;
import render.noosa.Game;
import render.utils.Bundle;
import render.utils.FileUtils;
import render.utils.Random;
import render.utils.SparseArray;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/** Headless verification for the six physical pages and their legacy acquisition chain. */
public final class SpsJournalPagesTest {

	private static final int CELL = 8 + 8 * 16;

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		FileUtils.setDefaultFileProperties(com.badlogic.gdx.Files.FileType.Absolute,
				System.getProperty("java.io.tmpdir") + "sps-journal-pages" + File.separator);
		Game.version = "test";
		Random.pushGenerator(0x4A4F55524E414C50L);
		try {
			testPagesAndBinding();
			testDolyaSlateCharge();
			testJournalPickup();
			testBossDrops();
			testChapterShops();
			testSourcesAndUtf8();
			System.out.println("SPS实体日志页测试通过：多利亚石板充能、六张纸片、日志拾取、四章首领掉落、商店来源、装订去重、存档与双语UTF-8均正常。");
		} finally {
			Random.popGenerator();
			Dungeon.hero = null;
			Dungeon.level = null;
			Dungeon.depth = 1;
			Dungeon.branch = 0;
			app.exit();
		}
	}

	private static void testDolyaSlateCharge() throws Exception {
		Dungeon.quickslot = new QuickSlot();
		Dungeon.hero = deadHero();
		Dungeon.level = new RecordingLevel();
		Dungeon.branch = 0;
		Dungeon.depth = 1;

		DolyaSlate slate = new DolyaSlate();
		Dungeon.hero.belongings.backpack.items.add(slate);
		check(slate.image == ItemSpriteSheet.DOLYA_SLATE && slate.value() == 300,
				"多利亚石板没有使用旧版图标或旧版售价");
		check(slate.charge() == 0 && !slate.canUsePortal()
				&& !slate.actions(Dungeon.hero).contains(AdventureJournal.AC_READ),
				"空充能石板错误地允许传送");

		for (int i = 0; i < AdventureJournal.PORTAL_CHARGE; i++) slate.gainCharge();
		check(slate.charge() == AdventureJournal.PORTAL_CHARGE && slate.canUsePortal()
				&& slate.actions(Dungeon.hero).contains(AdventureJournal.AC_READ),
				"石板达到500点后仍未开放传送");
		check(slate.addPage(new SafeSpotPage()) && slate.charge() == AdventureJournal.FULL_CHARGE,
				"首次装订页面没有把石板充满");
		check(slate.addPage(new Sokoban1()) && slate.charge() == AdventureJournal.FULL_CHARGE,
				"连续装订让充能超过1000点上限");

		Bundle saved = new Bundle();
		slate.storeInBundle(saved);
		DolyaSlate restored = new DolyaSlate();
		restored.restoreFromBundle(saved);
		check(restored.charge() == AdventureJournal.FULL_CHARGE,
				"多利亚石板充能没有写入存档");
		Method consume = AdventureJournal.class.getDeclaredMethod("consumePortalCharge");
		consume.setAccessible(true);
		consume.invoke(restored);
		check(restored.charge() == AdventureJournal.PORTAL_CHARGE, "传送没有消耗500点充能");
		consume.invoke(restored);
		check(restored.charge() == 0 && !restored.canUsePortal(), "石板耗尽后仍错误地允许传送");
		Field completed = AdventureJournal.class.getDeclaredField("completedMask");
		completed.setAccessible(true);
		completed.setInt(slate, 1 << 7);
		consume.invoke(slate);
		consume.invoke(slate);
		check(slate.charge() == 0 && slate.canUsePortal(), "救出Otiluke后没有绕过充能门槛");

		Dungeon.hero.belongings.backpack.items.clear();
		DolyaSlate ticking = new DolyaSlate();
		Dungeon.hero.belongings.backpack.items.add(ticking);
		Dungeon.hero.spend(1f);
		check(ticking.charge() == 1, "主地牢行动没有为石板充能");
		Dungeon.branch = 1;
		Dungeon.hero.spend(1f);
		check(ticking.charge() == 1, "支线行动错误地为石板充能");
		Dungeon.branch = 0;
		Dungeon.depth = 26;
		Dungeon.hero.spend(1f);
		check(ticking.charge() == 1, "26层及以后错误地为石板充能");
	}

	private static void testPagesAndBinding() {
		JournalPage[] pages = {
				new SafeSpotPage(), new Sokoban1(), new Sokoban2(),
				new Sokoban3(), new Sokoban4(), new Town()
		};
		AdventureJournal journal = new AdventureJournal();
		check(!journal.isUnlocked(0) && !journal.isUnlocked(5), "新日志在装订前已经开放房屋或城镇");
		ScrollHolder holder = new ScrollHolder();
		for (int i = 0; i < pages.length; i++) {
			JournalPage page = pages[i];
			check(page.destination() == i, page.getClass().getSimpleName() + "目的地编号错误");
			check(page.image == ItemSpriteSheet.SPS_JOURNAL_PAGE, page.getClass().getSimpleName() + "没有使用旧版纸片图标");
			check(page.value() == 150 && page.unique && holder.canHold(page), page.getClass().getSimpleName() + "基础属性或卷轴筒收纳规则错误");
			check(journal.addPage(page) && journal.isUnlocked(i), page.getClass().getSimpleName() + "首次装订失败");
			check(!journal.addPage(page), page.getClass().getSimpleName() + "允许重复装订");
		}

		Bundle saved = new Bundle();
		journal.storeInBundle(saved);
		AdventureJournal restored = new AdventureJournal();
		restored.restoreFromBundle(saved);
		for (int i = 0; i < pages.length; i++) check(restored.isUnlocked(i), "目的地" + i + "存档恢复后丢失");

		Dungeon.quickslot = new QuickSlot();
		Dungeon.hero = deadHero();
		Dungeon.level = new RecordingLevel();
		AdventureJournal gated = new AdventureJournal();
		gated.addPage(new Sokoban1());
		Dungeon.hero.belongings.backpack.items.add(gated);
		check(AdventureJournal.complete(1) && !gated.isUnlocked(2), "完成推箱教程绕过实体Sokoban2纸片");
		Dungeon.hero.belongings.backpack.items.clear();
		AdventureJournal expedition = new AdventureJournal();
		expedition.addPage(new NewHome());
		Dungeon.hero.belongings.backpack.items.add(expedition);
		check(AdventureJournal.complete(8) && expedition.isUnlocked(9), "新居完成后没有接入后续远征路线");
	}

	private static void testJournalPickup() {
		Dungeon.quickslot = new QuickSlot();
		Hero hero = deadHero();
		hero.pos = CELL;
		Dungeon.hero = hero;
		RecordingLevel level = new RecordingLevel();
		Dungeon.level = level;
		AdventureJournal journal = new AdventureJournal();
		check(journal.doPickUp(hero, CELL), "第6层购买日志后无法拾取");
		check(hero.belongings.getItem(AdventureJournal.class) == journal, "拾取的日志没有进入背包");
		check(count(level, SafeSpotPage.class) == 1 && count(level, LuckyBadge.class) == 1,
				"首次拾取日志没有同时生成房契和幸运徽章");
		check(Statistics.roomType >= 0 && Statistics.roomType <= 2, "房屋类型超出旧版三种布局");
	}

	private static void testBossDrops() {
		Dungeon.hero = deadHero();
		Dungeon.depth = 1;
		RecordingLevel prison = new RecordingLevel();
		Dungeon.level = prison;
		SpsPrisonBossRewards.grant(CELL, null, null);
		check(count(prison, Sokoban2.class) == 1, "监狱三选一首领没有掉落Sokoban2");

		RecordingLevel caves = new RecordingLevel();
		caves.locked = true;
		Dungeon.level = caves;
		SpsCavesBossRewards.grant(CELL, null, null);
		check(count(caves, Sokoban3.class) == 1, "洞穴三选一首领没有掉落Sokoban3");

		RecordingLevel city = new RecordingLevel();
		city.locked = true;
		Dungeon.level = city;
		SpsCityBossRewards.grant(CELL, 1, 1, null, null);
		check(count(city, Sokoban4.class) == 1, "都市三选一首领没有掉落Sokoban4");
	}

	@SuppressWarnings("unchecked")
	private static void testChapterShops() throws Exception {
		Method ensureItems = SpsShopRoom.class.getDeclaredMethod("ensureItems");
		ensureItems.setAccessible(true);
		Field itemsField = ShopRoom.class.getDeclaredField("itemsToSpawn");
		itemsField.setAccessible(true);

		Dungeon.depth = 6;
		SpsShopRoom journalShop = new SpsShopRoom();
		ensureItems.invoke(journalShop);
		ArrayList<Item> depthSix = (ArrayList<Item>)itemsField.get(journalShop);
		check(contains(depthSix, DolyaSlate.class) && !contains(depthSix, Town.class),
				"第6层商店没有出售多利亚石板或提前出售了城镇页");

		Dungeon.depth = 11;
		SpsShopRoom townShop = new SpsShopRoom();
		ensureItems.invoke(townShop);
		ArrayList<Item> depthEleven = (ArrayList<Item>)itemsField.get(townShop);
		check(contains(depthEleven, Town.class) && !contains(depthEleven, AdventureJournal.class),
				"第11层商店没有出售城镇页或重复出售日志");
	}

	private static void testSourcesAndUtf8() throws Exception {
		Path mobs = Path.of("..", "java", "pd", "actors", "mobs");
		for (String file : Arrays.asList("SpsGoo.java", "SewerHeart.java", "PlagueDoctor.java")) {
			String source = Files.readString(mobs.resolve(file), StandardCharsets.UTF_8);
			check(source.contains("Sokoban1()"), file + "仍未接入Sokoban1实体掉落");
			check(!source.contains("unlock(1)"), file + "仍绕过实体纸片直接解锁");
		}
		String yog = Files.readString(mobs.resolve("SpsYog.java"), StandardCharsets.UTF_8);
		check(!yog.contains("unlock(5)"), "Yog仍错误地直接开放城镇路线");
		String heroClass = Files.readString(Path.of("..", "java", "pd", "actors", "hero", "HeroClass.java"), StandardCharsets.UTF_8);
		check(!heroClass.contains("new AdventureJournal().collect()"), "普通职业仍在开局免费获得日志");
		String dolya = Files.readString(Path.of("..", "java", "pd", "items", "DolyaSlate.java"), StandardCharsets.UTF_8);
		check(dolya.contains("extends AdventureJournal") && dolya.contains("DOLYA_SLATE"),
				"多利亚石板实体类或旧版图标接线缺失");

		String en = Files.readString(Path.of("messages", "items", "en", "items.properties"), StandardCharsets.UTF_8);
		String zh = Files.readString(Path.of("messages", "items", "zh", "items.properties"), StandardCharsets.UTF_8);
		for (String key : Arrays.asList("safespotpage", "sokoban1", "sokoban2", "sokoban3", "sokoban4", "town")) {
			check(en.contains("items.journalpages." + key + ".name=")
					&& zh.contains("items.journalpages." + key + ".name="), key + "缺少英文或简体中文文本");
		}
		check(en.contains("items.dolyaslate.name=") && en.contains("items.dolyaslate.charge=")
				&& zh.contains("items.dolyaslate.name=") && zh.contains("items.dolyaslate.charge="),
				"多利亚石板缺少英文或简体中文文本");
		check(en.indexOf('\uFFFD') < 0 && zh.indexOf('\uFFFD') < 0, "日志页资源出现乱码替代字符");
	}

	private static Hero deadHero() {
		Hero hero = new Hero();
		hero.HT = 1;
		hero.HP = 0;
		return hero;
	}

	private static int count(Level level, Class<? extends Item> type) {
		int result = 0;
		for (Heap heap : level.heaps.values()) for (Item item : heap.items) if (type.isInstance(item)) result += item.quantity();
		return result;
	}

	private static boolean contains(ArrayList<Item> items, Class<? extends Item> type) {
		for (Item item : items) if (type.isInstance(item)) return true;
		return false;
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private static final class RecordingLevel extends Level {
		RecordingLevel() {
			setSize(16, 16);
			Arrays.fill(map, Terrain.EMPTY);
			mobs = new HashSet<>();
			heaps = new SparseArray<>();
			blobs = new HashMap<>();
			plants = new SparseArray<Plant>();
			traps = new SparseArray<Trap>();
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
			ItemSprite sprite = heap.sprite;
			heap.sprite = null;
			heap.drop(item);
			heap.sprite = sprite == null ? new ItemSprite() {
				@Override public void drop() { }
				@Override public void drop(int from) { }
			} : sprite;
			return heap;
		}
	}

	private SpsJournalPagesTest() { }
}
