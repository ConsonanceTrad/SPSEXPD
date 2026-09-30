package pd.items.artifacts.fusion;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.QuickSlot;
import pd.actors.Actor;
import pd.actors.buffs.ArmorBreak;
import pd.actors.buffs.Chill;
import pd.actors.buffs.Frost;
import pd.actors.buffs.FrostIce;
import pd.actors.buffs.Poison;
import pd.actors.hero.Hero;
import pd.actors.mobs.Gnoll;
import pd.items.Generator;
import pd.items.Heap;
import pd.items.Item;
import pd.items.StoneOre;
import pd.items.nornstone.NornStone;
import pd.journal.SpsCatalog;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.traps.Trap;
import pd.plants.Plant;
import pd.sprites.ItemSpriteSheet;
import render.noosa.Game;
import render.utils.Bundle;
import render.utils.FileUtils;
import render.utils.SparseArray;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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

/** Runtime parity checks for SPS-PD 0.9.8's Eye of Skadi. */
public final class SpsEyeOfSkadiTest {

	private static final String ICON_HASH = "2301EC3D3ACCFC4244E9049BF8C01267BD04FA11A3F2234DDF79D7599D10B157";

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		FileUtils.setDefaultFileProperties(com.badlogic.gdx.Files.FileType.Absolute,
				System.getProperty("java.io.tmpdir") + "sps-eye-of-skadi" + File.separator);
		Game.version = "test";
		try {
			testActionsAndRecharge();
			testCurse();
			testBlastAndLowHealthSafety();
			testSacrificeAndPersistence();
			testSourcesResourcesAndSprite();
			System.out.println("SPS斯嘉蒂之眼测试通过：诅咒、冰暴、献祭、充能、存档、来源、四语文本和原始图标均符合0.9.8。");
		} finally {
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			app.exit();
		}
	}

	private static void testActionsAndRecharge() {
		RecordingHero hero = prepareHero();
		TestEye eye = equip(hero);
		check(eye.actions(hero).contains(EyeOfSkadi.AC_ADD), "零级已装备冰眼缺少献祭动作");
		check(!eye.actions(hero).contains(EyeOfSkadi.AC_CURSE)
				&& !eye.actions(hero).contains(EyeOfSkadi.AC_BLAST), "零级空充能冰眼错误显示主动动作");
		eye.setCharge(EyeOfSkadi.FULL_CHARGE);
		check(eye.actions(hero).contains(EyeOfSkadi.AC_CURSE), "满充能冰眼缺少诅咒动作");
		eye.level(2);
		check(eye.actions(hero).contains(EyeOfSkadi.AC_BLAST), "二级冰眼缺少耗竭冰暴动作");
		eye.setCursed(true);
		check(!eye.actions(hero).contains(EyeOfSkadi.AC_ADD)
				&& !eye.actions(hero).contains(EyeOfSkadi.AC_CURSE)
				&& !eye.actions(hero).contains(EyeOfSkadi.AC_BLAST), "诅咒冰眼仍显示旧版主动动作");

		eye = equip(hero);
		for (int i = 0; i < 9; i++) eye.advanceCharge();
		check(eye.charge() == 0 && close(eye.partial(), 9f), "零级冰眼在十回合前提前充能");
		eye.advanceCharge();
		check(eye.charge() == 1 && close(eye.partial(), 0f), "零级冰眼没有每十回合增加一点充能");
		eye.level(10);
		eye.advanceCharge();
		check(eye.charge() == 2 && close(eye.partial(), 0f), "十级冰眼没有每回合增加一点充能");
		eye.setCursed(true);
		eye.setPartial(7f);
		eye.advanceCharge();
		check(eye.charge() == 2 && close(eye.partial(), 0f), "诅咒冰眼仍充能或没有清空部分充能");
	}

	private static void testCurse() {
		RecordingHero hero = prepareHero();
		TestEye eye = equip(hero);
		eye.level(3);
		eye.setCharge(EyeOfSkadi.FULL_CHARGE);
		Gnoll victim = mob(105, 100);
		Dungeon.level.mobs.add(victim);
		Actor.add(victim);
		check(!eye.curse(-1, hero) && eye.charge() == EyeOfSkadi.FULL_CHARGE,
				"越界目标错误触发冰眼诅咒");
		check(!eye.curse(victim.pos, hero) && eye.charge() == EyeOfSkadi.FULL_CHARGE,
				"未探索目标错误触发冰眼诅咒");
		Dungeon.level.visited[victim.pos] = true;
		check(eye.curse(victim.pos, hero), "已探索角色无法成为冰眼诅咒目标");
		check(eye.charge() == 0 && close(hero.spent, 1f), "诅咒没有清空充能或消耗一回合");
		check(victim.buff(Poison.class) != null
				&& victim.buff(FrostIce.class) != null && close(victim.buff(FrostIce.class).level(), 12f)
				&& victim.buff(ArmorBreak.class) != null && victim.buff(ArmorBreak.class).level() == 80
				&& victim.buff(Chill.class) != null,
				"诅咒没有施加毒、中冰、80%破甲和寒冷");
	}

	private static void testBlastAndLowHealthSafety() {
		RecordingHero hero = prepareHero();
		TestEye eye = equip(hero);
		eye.level(4);
		Gnoll normal = mob(110, 100);
		Gnoll low = mob(111, 1);
		Dungeon.level.mobs.add(normal);
		Dungeon.level.mobs.add(low);
		Actor.add(normal);
		Actor.add(low);
		check(eye.blast() == 2, "冰暴没有攻击全层两只怪物");
		check(normal.HP >= 51 && normal.HP <= 75 && normal.buff(Frost.class) != null,
				"冰暴伤害不在当前生命1/4至1/2区间或没有冻结");
		check(low.HP == 1 && low.buff(Frost.class) != null,
				"一生命怪物触发了无效随机区间或没有冻结");

		Dungeon.level.mobs.clear();
		eye.level(3);
		eye.execute(hero, EyeOfSkadi.AC_BLAST);
		check(eye.level() == 2 && close(hero.spent, 0f), "耗竭冰暴没有降低一级或错误消耗回合");
		Dungeon.level = null;
		check(eye.blast() == 0, "空关卡执行冰暴没有安全结束");
	}

	private static void testSacrificeAndPersistence() throws Exception {
		RecordingHero hero = prepareHero();
		TestEye eye = equip(hero);
		StoneOre first = new StoneOre();
		StoneOre second = new StoneOre();
		hero.belongings.backpack.items.add(first);
		hero.belongings.backpack.items.add(second);
		check(eye.sacrifice(hero, first) && eye.level() == 0 && eye.consumedPoints() == 1,
				"第一块原石不应越过严格升级边界");
		check(eye.sacrifice(hero, second) && eye.level() == 1 && eye.consumedPoints() == 2,
				"第二块原石没有按严格大于等级加一的条件升级");
		NornStone norn = new NornStone();
		hero.belongings.backpack.items.add(norn);
		check(eye.sacrifice(hero, norn) && eye.level() == 2 && eye.consumedPoints() == 7,
				"诺恩原石没有增加五点献祭经验并升级一次");
		check(close(hero.spent, 6f), "三次献祭没有各消耗两回合");

		TestEye independent = new TestEye();
		check(independent.consumedPoints() == 0, "多个冰眼仍共享原版静态献祭进度缺陷");
		Field consumed = EyeOfSkadi.class.getDeclaredField("consumedPoints");
		check(!Modifier.isStatic(consumed.getModifiers()), "献祭进度字段仍为静态字段");

		eye.setCharge(73);
		eye.setPartial(6f);
		Bundle saved = new Bundle();
		eye.storeInBundle(saved);
		saved.put("partialCharge", 7f);
		TestEye restored = new TestEye();
		restored.restoreFromBundle(saved);
		check(restored.level() == 2 && restored.charge() == 73
				&& close(restored.partial(), 7f) && restored.consumedPoints() == 7,
				"等级、充能、旧部分充能字段或献祭进度读档错误");
	}

	private static void testSourcesResourcesAndSprite() throws Exception {
		check(Arrays.asList(Generator.Category.ARTIFACT.classes).contains(EyeOfSkadi.class),
				"斯嘉蒂之眼没有接入神器池");
		check(SpsCatalog.contains(EyeOfSkadi.class), "斯嘉蒂之眼没有接入旧版目录");
		String journal = Files.readString(Path.of("../java/pd/items/quest/AdventureJournal.java"));
		check(journal.contains("case 20: reward = new EyeOfSkadi();"), "冒险日志第20项奖励不是斯嘉蒂之眼");
		String game = Files.readString(Path.of("../java/pd/ShatteredPixelDungeon.java"));
		check(game.contains("com.hmdzl.spspd.items.artifacts.EyeOfSkadi\"")
				&& game.contains("com.hmdzl.spspd.items.artifacts.EyeOfSkadi$eyeRecharge"),
				"缺少0.9.8冰眼或充能被动旧类名存档别名");
		String source = Files.readString(Path.of("../java/pd/items/artifacts/fusion/EyeOfSkadi.java"));
		check(source.contains("partialCharge += 1 + level()")
				&& source.contains("consumedPoints > level() + 1")
				&& source.contains("DamageType.ICE_DAMAGE")
				&& !source.contains("extends TalismanOfForesight"),
				"冰眼充能、献祭、冰伤或独立实现发生偏移");

		for (String file : new String[]{"en/items.properties", "zh/items.properties",
				"zh-hant/items.properties", "ru/items.properties"}) {
			Properties items = load("messages/items/" + file);
			for (String key : new String[]{"name", "ac_add", "ac_blast", "ac_curse", "no_charge",
					"prompt", "need_charge", "full_charge", "exp", "infuse_ore", "desc"}) {
				required(items, "items.artifacts.fusion.eyeofskadi." + key, file);
			}
			check(items.getProperty("items.artifacts.fusion.eyeofskadi.exp").contains("%s"),
					file + "的献祭经验文本缺少占位符");
		}
		Properties zh = load("messages/items/zh/items.properties");
		check("斯嘉蒂之眼".equals(zh.getProperty("items.artifacts.fusion.eyeofskadi.name"))
				&& "耗竭-冰暴".equals(zh.getProperty("items.artifacts.fusion.eyeofskadi.ac_blast")),
				"斯嘉蒂之眼简体中文不是旧版文本或出现乱码");

		BufferedImage current = ImageIO.read(Path.of("sprites/items", "items.png").toFile());
		//外部 0.9.8 基准缺失时回退到仓库内置参考源码
		java.nio.file.Path legacyPng = Path.of("..", "..", "..", "..", "..",
				"SPS-PD-0.9.8", "SPS-PD-0.9.8", "assets", "items.png");
		if (!legacyPng.toFile().exists()) legacyPng = Path.of("..", "..", "..", "..", "_ref", "ref", "SPS-PD", "assets", "items.png");
		BufferedImage legacy = ImageIO.read(legacyPng.toFile());
		for (int y = 0; y < 16; y++) {
			for (int x = 0; x < 16; x++) {
				check(current.getRGB(80 + x, 960 + y) == legacy.getRGB(128 + x, 288 + y),
						"斯嘉蒂之眼图标不是0.9.8原始像素：" + x + "," + y);
			}
		}
		check(ICON_HASH.equals(hash(current, ItemSpriteSheet.ARTIFACT_ICE_EYE)), "斯嘉蒂之眼原始图标哈希错误");
		check(new EyeOfSkadi().image == ItemSpriteSheet.ARTIFACT_ICE_EYE,
				"斯嘉蒂之眼没有使用专用图标常量");

		try (java.util.stream.Stream<Path> paths = Files.walk(Path.of("messages"))) {
			for (Path path : (Iterable<Path>) paths.filter(p -> p.toString().endsWith(".properties"))::iterator) {
				String text = Files.readString(path, StandardCharsets.UTF_8);
				check(!text.contains("\uFFFD"), "资源含UTF-8替换字符：" + path);
			}
		}
	}

	private static RecordingHero prepareHero() {
		Actor.clear();
		Dungeon.quickslot = new QuickSlot();
		Dungeon.level = new TestLevel();
		RecordingHero hero = new RecordingHero();
		hero.HP = hero.HT = 100;
		hero.pos = 100;
		Dungeon.hero = hero;
		Actor.add(hero);
		return hero;
	}

	private static TestEye equip(Hero hero) {
		TestEye eye = new TestEye();
		eye.identify();
		hero.belongings.artifact = eye;
		return eye;
	}

	private static Gnoll mob(int position, int health) {
		Gnoll mob = new Gnoll();
		mob.pos = position;
		mob.HP = mob.HT = health;
		return mob;
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

	private static String hash(BufferedImage sheet, int itemIndex) throws Exception {
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

	private static boolean close(float actual, float expected) { return Math.abs(actual - expected) < 0.0001f; }
	private static void check(boolean condition, String message) { if (!condition) throw new AssertionError(message); }

	private static final class RecordingHero extends Hero {
		float spent;
		@Override public void spend(float time) { spent += time; }
		@Override public void spendAndNext(float time) { spent += time; }
	}

	private static final class TestEye extends EyeOfSkadi {
		void setCharge(int value) { charge = value; }
		void setPartial(float value) { partialCharge = value; }
		float partial() { return partialCharge; }
		void setCursed(boolean value) { cursed = value; }
	}

	private static final class TestLevel extends Level {
		TestLevel() {
			setSize(32, 32);
			mobs = new HashSet<>(); heaps = new SparseArray<>(); blobs = new HashMap<>();
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

	private SpsEyeOfSkadiTest() { }
}
