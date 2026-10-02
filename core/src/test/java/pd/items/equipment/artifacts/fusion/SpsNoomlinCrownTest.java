package pd.items.equipment.artifacts.fusion;

import pd.atlas.items.SpecificPlaceHolderDict;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.QuickSlot;
import pd.actors.Actor;
import pd.actors.hero.Hero;
import pd.items.Heap;
import pd.items.Item;
import pd.journal.SpsCatalog;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.traps.Trap;
import pd.plants.Plant;
import render.noosa.Game;
import render.utils.data.SparseArray;
import render.utils.serialize.Bundle;
import render.utils.serialize.FileUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;

import javax.imageio.ImageIO;

/** Runtime parity checks for SPS-PD 0.9.8's Noomlin Crown. */
public final class SpsNoomlinCrownTest {

	private static final String ICON_HASH = "0D1FB5652FB2E62F62AF83458F9F2C558C96FC527D115FC9AB3154A0DE587B87";

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		FileUtils.setDefaultFileProperties(com.badlogic.gdx.Files.FileType.Absolute,
				System.getProperty("java.io.tmpdir") + "sps-noomlin-crown" + File.separator);
		Game.version = "test";
		try {
			testPowerlessArtifact();
			testSaveSourcesResourcesAndSprite();
			System.out.println("SPS诺姆林王冠测试通过：空被动、一级上限、售价、存档、来源、四语文本和原始图标均符合0.9.8。");
		} finally {
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			app.exit();
		}
	}

	private static void testPowerlessArtifact() {
		Hero hero = prepareHero();
		TestCrown crown = new TestCrown();
		crown.identify();
		hero.belongings.artifact = crown;
		check(crown.cap() == 1, "诺姆林王冠等级上限不是1");
		check(crown.value() == 100 && !crown.isUpgradable(), "诺姆林王冠售价或不可升级属性错误");
		check(crown.status() == null, "无力量的王冠错误显示充能状态");
		for (String action : crown.actions(hero)) {
			check(!"STEAL".equals(action) && !"LEVY".equals(action), "王冠仍保留错误的神偷袖章动作");
		}
		crown.activate(hero);
		check(hero.buff(NoomlinCrown.Crown.class) != null, "王冠空被动没有随装备激活");
	}

	private static void testSaveSourcesResourcesAndSprite() throws Exception {
		TestCrown crown = new TestCrown();
		Bundle saved = new Bundle();
		crown.storeInBundle(saved);
		TestCrown restored = new TestCrown();
		restored.restoreFromBundle(saved);
		check(restored.level() == 0 && restored.value() == 100, "王冠基础存档或售价读档错误");
		check(SpsCatalog.contains(NoomlinCrown.class), "诺姆林王冠没有接入旧版目录");

		String shop = Files.readString(Path.of("../java/pd/levels/rooms/special/SpsShopRoom.java"));
		String journal = Files.readString(Path.of("../java/pd/items/quest/AdventureJournal.java"));
		String game = Files.readString(Path.of("../java/pd/ShatteredPixelDungeon.java"));
		String source = Files.readString(Path.of("../java/pd/items/artifacts/fusion/NoomlinCrown.java"));
		check(shop.contains("new NoomlinCrown()"), "旧版特殊商店没有出售诺姆林王冠");
		check(journal.contains("case 23: reward = new NoomlinCrown();"), "冒险日志第23项奖励不是诺姆林王冠");
		check(game.contains("com.hmdzl.spspd.items.artifacts.NoomlinCrown\"")
				&& game.contains("com.hmdzl.spspd.items.artifacts.NoomlinCrown$crown"),
				"缺少0.9.8王冠或空被动旧类名存档别名");
		check(source.contains("levelCap = 1") && source.contains("return new Crown()")
				&& !source.contains("MasterThievesArmband"), "王冠一级上限、空被动或独立实现发生偏移");

		for (String file : new String[]{"en/items.properties", "zh/items.properties",
				"zh-hant/items.properties", "ru/items.properties"}) {
			Properties items = load("messages/items/" + file);
			required(items, "items.artifacts.fusion.noomlincrown.name", file);
			required(items, "items.artifacts.fusion.noomlincrown.desc", file);
			check(items.getProperty("items.artifacts.fusion.noomlincrown.ac_steal") == null,
					file + "仍保留错误的征收动作文本");
		}
		Properties zh = load("messages/items/zh/items.properties");
		check("诺姆林王冠".equals(zh.getProperty("items.artifacts.fusion.noomlincrown.name"))
				&& zh.getProperty("items.artifacts.fusion.noomlincrown.desc").contains("没有力量"),
				"诺姆林王冠简体中文不是旧版文本或出现乱码");

		BufferedImage current = ImageIO.read(Path.of("sprites/items", "items.png").toFile());
		//外部 0.9.8 基准缺失时回退到仓库内置参考源码
		java.nio.file.Path legacyPng = Path.of("..", "..", "..", "..",
				"SPS-PD-0.9.8", "SPS-PD-0.9.8", "assets", "items.png");
		if (!legacyPng.toFile().exists()) legacyPng = Path.of("..", "..", "..", "_ref", "ref", "SPS-PD", "assets", "items.png");
		BufferedImage legacy = ImageIO.read(legacyPng.toFile());
		for (int y = 0; y < 16; y++) {
			for (int x = 0; x < 16; x++) {
				check(current.getRGB(96 + x, 960 + y) == legacy.getRGB(304 + x, 288 + y),
						"诺姆林王冠图标不是0.9.8原始像素：" + x + "," + y);
			}
		}
		check(ICON_HASH.equals(hash(current, SpecificPlaceHolderDict.SOMETHING_0)), "诺姆林王冠原始图标哈希错误");
		check(new NoomlinCrown().image == SpecificPlaceHolderDict.SOMETHING_0,
				"诺姆林王冠没有使用专用图标常量");
	}

	private static Hero prepareHero() {
		Actor.clear();
		Dungeon.quickslot = new QuickSlot();
		Dungeon.level = new TestLevel();
		Hero hero = new Hero();
		hero.HP = hero.HT = 100;
		hero.pos = 100;
		Dungeon.hero = hero;
		Actor.add(hero);
		return hero;
	}

	private static Properties load(String path) throws Exception {
		Properties properties = new Properties();
		try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(Path.of(path)), StandardCharsets.UTF_8)) {
			properties.load(reader);
		}
		return properties;
	}

	private static void required(Properties properties, String key, String file) {
		check(properties.getProperty(key) != null && !properties.getProperty(key).isEmpty(),
				file + "缺少文本：" + key);
	}

	private static String hash(BufferedImage sheet, IconEntry itemIndex) throws Exception {
		int left = (itemIndex % 16) * 16;
		int top = (itemIndex / 16) * 16;
		ByteBuffer pixels = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
		for (int y = top; y < top + 16; y++) {
			for (int x = left; x < left + 16; x++) pixels.putInt(sheet.getRGB(x, y));
		}
		byte[] digest = MessageDigest.getInstance("SHA-256").digest(pixels.array());
		StringBuilder out = new StringBuilder(64);
		for (byte value : digest) out.append(String.format("%02X", value & 0xFF));
		return out.toString();
	}

	private static void check(boolean condition, String message) { if (!condition) throw new AssertionError(message); }

	private static final class TestCrown extends NoomlinCrown {
		int cap() { return levelCap; }
	}

	private static final class TestLevel extends Level {
		TestLevel() {
			setSize(32, 32);
			mobs().clear(); heaps = new SparseArray<>(); blobs = new HashMap<>();
			plants = new SparseArray<Plant>(); traps = new SparseArray<Trap>(); transitions = new ArrayList<>();
			customTiles = new ArrayList<>(); customTerrain = new ArrayList<>(); customWalls = new ArrayList<>();
			Arrays.fill(map, Terrain.EMPTY); Arrays.fill(passable, true); buildFlagMaps();
		}
		@Override protected boolean build() { return true; }
		@Override protected void createMobs() { }
		@Override protected void createItems() { }
		@Override public Heap drop(Item item, int cell) { return null; }
		@Override public String tilesTex() { return null; }
		@Override public String waterTex() { return null; }
	}

	private SpsNoomlinCrownTest() { }
}
