package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.QuickSlot;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.FileUtils;
import com.watabou.utils.SparseArray;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;

/** Runtime parity checks for the SPS-PD 0.9.8 Lloyd's beacon. */
public final class SpsLloydsBeaconTest {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		FileUtils.setDefaultFileProperties(Files.FileType.Absolute,
				System.getProperty("java.io.tmpdir") + "sps-lloyds-beacon" + File.separator);
		Game.version = "test";
		try {
			testIdentityAndActions();
			testSaveAndArtifactMigration();
			testSetAndUseRestrictions();
			testOccupiedReturnCell();
			testBossHooksRemoved();
			testLocalizedResources();
			System.out.println("SPS时空道标通过：普通唯一物品、设置/返回、邻近角色与楼层限制、存档迁移、占位保护和四语文本均符合0.9.8。");
		} finally {
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			app.exit();
		}
	}

	private static void testIdentityAndActions() {
		LloydsBeacon beacon = new LloydsBeacon();
		Hero hero = new Hero();
		check(LloydsBeacon.class.getSuperclass() == Item.class,
				"时空道标仍是需要装备和充能的破碎版神器");
		check(beacon.unique && beacon.image == ItemSpriteSheet.BEACON,
				"时空道标没有恢复0.9.8唯一属性或原版图标");
		check(!beacon.isUpgradable() && beacon.isIdentified(),
				"时空道标没有保持不可升级和永久鉴定");
		check(!beacon.usesTargeting && beacon.defaultAction() == null,
				"时空道标仍要求瞄准或保留了默认射击动作");
		ArrayList<String> actions = beacon.actions(hero);
		check(actions.contains(LloydsBeacon.AC_SET) && !actions.contains(LloydsBeacon.AC_RETURN)
				&& !actions.contains("ZAP"), "未设置的时空道标动作不符合0.9.8");
	}

	private static void testSaveAndArtifactMigration() {
		Dungeon.quickslot = new QuickSlot();
		Bundle oldArtifact = new Bundle();
		oldArtifact.put("quantity", 1);
		oldArtifact.put("level", 3);
		oldArtifact.put("levelKnown", true);
		oldArtifact.put("cursedKnown", true);
		oldArtifact.put("exp", 9);
		oldArtifact.put("charge", 3);
		oldArtifact.put("partialcharge", 0.75f);
		oldArtifact.put("depth", 12);
		oldArtifact.put("pos", 345);

		LloydsBeacon beacon = new LloydsBeacon();
		beacon.restoreFromBundle(oldArtifact);
		check(beacon.level() == 0, "破碎版神器存档的升级等级没有在迁移时清除");
		check(beacon.actions(new Hero()).contains(LloydsBeacon.AC_RETURN)
				&& beacon.glowing() != null && beacon.desc().contains("12"),
				"时空道标没有恢复深度、位置提示或白色光效");

		Bundle saved = new Bundle();
		beacon.storeInBundle(saved);
		check(saved.getInt("depth") == 12 && saved.getInt("pos") == 345,
				"时空道标没有保存depth/pos");
		check(!saved.contains("exp") && !saved.contains("charge")
				&& !saved.contains("partialcharge"), "时空道标仍在写入神器充能字段");

		beacon.reset();
		check(!beacon.actions(new Hero()).contains(LloydsBeacon.AC_RETURN)
				&& beacon.glowing() == null, "时空道标重置后仍保留返回点");
	}

	private static void testSetAndUseRestrictions() {
		Hero hero = prepare(4, 100);
		LloydsBeacon beacon = new LloydsBeacon();
		beacon.execute(hero, new String(LloydsBeacon.AC_SET));
		check(beacon.actions(hero).contains(LloydsBeacon.AC_RETURN),
				"时空道标没有接受等值动作字符串或没有设置返回点");
		Bundle set = new Bundle();
		beacon.storeInBundle(set);
		check(set.getInt("depth") == 4 && set.getInt("pos") == 100,
				"时空道标设置了错误的深度或位置");

		beacon = new LloydsBeacon();
		DummyMob ally = new DummyMob();
		ally.alignment = Char.Alignment.ALLY;
		ally.pos = hero.pos + 1;
		Actor.add(ally);
		beacon.execute(hero, LloydsBeacon.AC_SET);
		check(!beacon.actions(hero).contains(LloydsBeacon.AC_RETURN),
				"相邻友方角色没有像0.9.8一样阻止时空道标");

		hero = prepare(5, 100);
		beacon = new LloydsBeacon();
		beacon.execute(hero, LloydsBeacon.AC_SET);
		check(!beacon.actions(hero).contains(LloydsBeacon.AC_RETURN),
				"首领层仍可设置时空道标");

		hero = prepare(26, 100);
		beacon = new LloydsBeacon();
		beacon.execute(hero, LloydsBeacon.AC_SET);
		check(!beacon.actions(hero).contains(LloydsBeacon.AC_RETURN),
				"深度大于24时仍可设置时空道标");
	}

	private static void testOccupiedReturnCell() {
		Hero hero = prepare(4, 100);
		LloydsBeacon beacon = new LloydsBeacon();
		Bundle saved = new Bundle();
		saved.put("quantity", 1);
		saved.put("depth", 4);
		saved.put("pos", 200);
		beacon.restoreFromBundle(saved);

		DummyMob occupant = new DummyMob();
		occupant.pos = 200;
		Dungeon.level.mobs.add(occupant);
		Actor.add(occupant);
		beacon.execute(hero, LloydsBeacon.AC_RETURN);
		check(hero.pos == 200 && occupant.pos != hero.pos,
				"返回点被占用时角色重叠或传送失败");
	}

	private static void testBossHooksRemoved() throws Exception {
		Path root = Path.of("..", "java", "com", "shatteredpixel", "shatteredpixeldungeon", "actors", "mobs");
		for (String name : new String[]{"Tengu.java", "DM300.java", "DwarfKing.java"}) {
			String source = java.nio.file.Files.readString(root.resolve(name), StandardCharsets.UTF_8);
			check(!source.contains("LloydsBeacon"), name + "仍含破碎版道标升级入口");
		}
		String source = java.nio.file.Files.readString(Path.of("..", "java", "com", "shatteredpixel",
				"shatteredpixeldungeon", "items", "artifacts", "LloydsBeacon.java"), StandardCharsets.UTF_8);
		check(source.contains("InterlevelScene.returnBranch = 0;"),
				"跨层返回没有清除现代分支残值，可能进入错误地图");
	}

	private static void testLocalizedResources() throws Exception {
		String[] files = {"items.properties", "items_zh.properties",
				"items_zh-hant.properties", "items_ru.properties"};
		String[] requiredKeys = {"name", "ac_set", "ac_return", "preventing",
				"creatures", "return", "desc", "desc_set"};
		for (String file : files) {
			Properties items = load("messages/items/" + file);
			for (String key : requiredKeys) {
				required(items, "items.artifacts.lloydsbeacon." + key, file);
			}
			for (String key : new String[]{"ac_zap", "no_charge", "tele_fail", "prompt", "levelup"}) {
				check(!items.containsKey("items.artifacts.lloydsbeacon." + key),
						file + "仍含破碎版道标键：" + key);
			}
		}

		try (java.util.stream.Stream<Path> paths = java.nio.file.Files.walk(Path.of("messages"))) {
			for (Path path : (Iterable<Path>)paths.filter(p -> p.toString().endsWith(".properties"))::iterator) {
				String text = java.nio.file.Files.readString(path, StandardCharsets.UTF_8);
				check(!text.contains("\uFFFD"), "资源含替换字符：" + path);
			}
		}
	}

	private static Hero prepare(int depth, int position) {
		Actor.clear();
		Dungeon.depth = depth;
		Dungeon.branch = 0;
		Dungeon.quickslot = new QuickSlot();
		Dungeon.level = new TestLevel();
		Hero hero = new Hero();
		hero.pos = position;
		Dungeon.hero = hero;
		Actor.add(hero);
		return hero;
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
		check(value != null && !value.isEmpty(), "缺少时空道标文本 " + key + "：" + file);
		return value;
	}

	private static final class DummyMob extends Mob {
		@Override public int damageRoll() { return 0; }
		@Override public int attackSkill(Char target) { return 0; }
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

	private SpsLloydsBeaconTest() { }
}
