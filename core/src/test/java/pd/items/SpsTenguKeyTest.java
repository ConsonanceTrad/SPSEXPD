package pd.items;

import pd.atlas.items.SpecificPlaceHolderDict;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Badges;
import pd.Dungeon;
import pd.QuickSlot;
import pd.actors.hero.Hero;
import pd.items.equipment.bags.KeyRing;
import pd.items.quest.AdventureJournal;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.features.LevelTransition;
import pd.levels.traps.Trap;
import pd.plants.Plant;
import pd.sprites.ItemSprite;
import render.noosa.Game;
import render.utils.data.SparseArray;
import render.utils.serialize.Bundle;
import render.utils.serialize.FileUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import javax.imageio.ImageIO;

/** Headless verification for the legacy prison-boss portal to TenguDen. */
public final class SpsTenguKeyTest {

	private static final int CELL = 8 + 8 * 16;
	private static final String ICON_HASH = "998220FE57B9E737CE668B26E3C723C489084DF319F731A7D0E1B8C7638AED5F";

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		FileUtils.setDefaultFileProperties(Files.FileType.Absolute,
				System.getProperty("java.io.tmpdir") + "sps-tengu-key" + File.separator);
		Game.version = "test";
		try {
			Badges.loadGlobal();
			testItemAndSaveState();
			testCompletionCompatibility();
			testPrisonBossSource();
			testSourceContractsAndResources();
			testLegacyIcon();
			System.out.println("SPS天狗钥匙测试通过：监狱三首领来源、天狗巢往返、击杀门槛、一次性消耗、存档迁移、双语文本和原始图标均正常。");
		} finally {
			Dungeon.hero = null;
			Dungeon.level = null;
			Dungeon.depth = 1;
			Dungeon.branch = 0;
			Dungeon.tenguDenKilled = false;
			app.exit();
		}
	}

	private static void testItemAndSaveState() {
		Hero hero = deadHero();
		TenguKey key = new TenguKey();
		check(key.image == SpecificPlaceHolderDict.SOMETHING_0 && key.destination() == 10,
				"天狗钥匙没有接入旧版图标或天狗巢目的地");
		check(key.unique && !key.stackable && key.isIdentified() && !key.isUpgradable(),
				"天狗钥匙基础属性与旧版不一致");
		check(key.actions(hero).contains(TenguKey.AC_PORT) && TenguKey.AC_PORT.equals(key.defaultAction),
				"天狗钥匙缺少默认传送动作");
		check(key.glowing() != null, "天狗钥匙缺少旧版黑色发光");
		check(new KeyRing().canHold(key), "钥匙环不能收纳天狗钥匙");

		Bundle state = new Bundle();
		key.storeInBundle(state);
		state.put("return_depth", 13);
		state.put("return_branch", 0);
		state.put("return_pos", 137);
		TenguKey restored = new TenguKey();
		restored.restoreFromBundle(state);
		Bundle roundTrip = new Bundle();
		restored.storeInBundle(roundTrip);
		check(roundTrip.getInt("return_depth") == 13
				&& roundTrip.getInt("return_branch") == 0
				&& roundTrip.getInt("return_pos") == 137,
				"天狗钥匙没有保存原楼层、分支和位置");
		restored.reset();
		Bundle reset = new Bundle();
		restored.storeInBundle(reset);
		check(reset.getInt("return_depth") == -1 && reset.getInt("return_pos") == -1,
				"天狗钥匙重置后仍保留旧回程位置");
	}

	private static void testCompletionCompatibility() {
		Dungeon.hero = deadHero();
		Dungeon.tenguDenKilled = false;
		TenguKey key = new TenguKey();
		check(!key.bossKilled(), "新存档错误地把天狗巢视为已完成");
		Dungeon.tenguDenKilled = true;
		check(key.bossKilled(), "独立天狗击杀标记没有开放钥匙回程");

		Dungeon.tenguDenKilled = false;
		AdventureJournal journal = new AdventureJournal();
		Dungeon.hero.belongings.backpack.items.add(journal);
		check(AdventureJournal.complete(10) && key.bossKilled(),
				"旧日志中的天狗完成记录没有迁移到钥匙回程判断");
	}

	private static void testPrisonBossSource() throws Exception {
		Dungeon.quickslot = new QuickSlot();
		Dungeon.hero = deadHero();
		Dungeon.depth = 10;
		RecordingLevel level = new RecordingLevel();
		Dungeon.level = level;
		Class<?> rewards = Class.forName("pd.actors.mobs.SpsPrisonBossRewards");
		Method grant = rewards.getDeclaredMethod("grant", int.class, Item.class, Item.class);
		grant.setAccessible(true);
		grant.invoke(null, CELL, null, null);
		check(count(level, TenguKey.class) == 1, "监狱首领公共奖励没有掉落天狗钥匙");
	}

	private static void testSourceContractsAndResources() throws Exception {
		Path javaRoot = Path.of("..", "java", "pd");
		for (String boss : new String[]{"SpsTengu.java", "Tank.java", "PrisonWander.java"}) {
			String source = java.nio.file.Files.readString(javaRoot.resolve(Path.of("actors", "mobs", boss)), StandardCharsets.UTF_8);
			check(source.contains("SpsPrisonBossRewards.grant"), boss + "没有接入天狗钥匙公共掉落");
		}
		String keyBase = java.nio.file.Files.readString(javaRoot.resolve(Path.of("items", "SpsBossKey.java")), StandardCharsets.UTF_8);
		check(keyBase.contains("Dungeon.depth <= 1 || Dungeon.depth >= 25")
				&& keyBase.contains("Dungeon.bossLevel()")
				&& keyBase.contains("!bossKilled()")
				&& keyBase.contains("detach(hero.belongings.backpack)"),
				"天狗钥匙缺少旧版进入范围、首领门槛或回程消耗");
		String dungeon = java.nio.file.Files.readString(javaRoot.resolve("Dungeon.java"), StandardCharsets.UTF_8);
		check(occurrences(dungeon, "TENGU_DEN_KILLED") >= 3,
				"天狗击杀状态没有完整接入初始化、写档和读档");
		String den = java.nio.file.Files.readString(javaRoot.resolve(Path.of("actors", "mobs", "TenguDen.java")), StandardCharsets.UTF_8);
		check(den.contains("Dungeon.tenguDenKilled = true") && den.contains("AdventureJournal.complete(10)"),
				"击杀天狗没有同时兼容钥匙与日志路线");

		String en = java.nio.file.Files.readString(Path.of("messages", "items", "en", "items.properties"), StandardCharsets.UTF_8);
		String zh = java.nio.file.Files.readString(Path.of("messages", "items", "zh", "items.properties"), StandardCharsets.UTF_8);
		for (String key : new String[]{"items.tengukey.name=", "items.tengukey.ac_port=", "items.tengukey.desc="}) {
			check(en.contains(key) && zh.contains(key), "天狗钥匙缺少中英文资源键：" + key);
		}
		check(zh.contains("匿藏地传送门") && !zh.contains("�"), "天狗钥匙中文资源乱码");
	}

	private static void testLegacyIcon() throws Exception {
		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		check(sheet.getHeight() >= 976, "物品图集没有为天狗钥匙保留完整的新行");
		check(ICON_HASH.equals(hash(sheet, 0, 960)), "天狗钥匙图标与SPS-PD 0.9.8原图不一致");
	}

	private static String hash(BufferedImage sheet, int left, int top) throws Exception {
		ByteBuffer pixels = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
		for (int y = top; y < top + 16; y++) {
			for (int x = left; x < left + 16; x++) pixels.putInt(sheet.getRGB(x, y));
		}
		byte[] digest = MessageDigest.getInstance("SHA-256").digest(pixels.array());
		StringBuilder out = new StringBuilder(64);
		for (byte value : digest) out.append(String.format("%02X", value & 0xFF));
		return out.toString();
	}

	private static int occurrences(String text, String token) {
		int count = 0;
		for (int at = 0; (at = text.indexOf(token, at)) >= 0; at += token.length()) count++;
		return count;
	}

	private static Hero deadHero() {
		Hero hero = new Hero();
		hero.HT = 1;
		hero.HP = 0;
		return hero;
	}

	private static int count(Level level, Class<? extends Item> type) {
		int result = 0;
		for (Heap heap : level.heaps.values()) {
			for (Item item : heap.items) if (type.isInstance(item)) result += item.quantity();
		}
		return result;
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private static final class RecordingLevel extends Level {
		RecordingLevel() {
			setSize(16, 16);
			Arrays.fill(map, Terrain.EMPTY);
			mobs().clear();
			heaps = new SparseArray<>();
			blobs = new HashMap<>();
			plants = new SparseArray<Plant>();
			traps = new SparseArray<Trap>();
			transitions = new ArrayList<LevelTransition>();
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

	private SpsTenguKeyTest() { }
}
