package pd.items.equipment.artifacts;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.hero.Hero;
import pd.actors.mobs.DecoySheep;
import pd.items.Generator;
import pd.levels.Level;
import pd.plants.Plant;
import render.noosa.Game;
import render.utils.data.SparseArray;
import render.utils.math.Random;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

/** Headless checks for the ported cloak of sheep: blink range/growth, cooldown and the decoy sheep. */
public final class SpsCloakOfSheepTest {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Random.pushGenerator(0x5350535348454550L);
		try {
			testStatsAndActions();
			testCooldownAndDecoy();
			testPoolAndMessages();
			System.out.println("SPS绵羊披风测试通过：闪烁距离与成长门槛、冷却就绪判定、动作开放、替身绵羊寿命、神器池与双语文本均正常。");
		} finally {
			Random.popGenerator();
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			app.exit();
		}
	}

	private static void testStatsAndActions() {
		Dungeon.level = new TestLevel();
		Hero hero = new Hero();
		hero.pos = 27;
		Dungeon.hero = hero;

		CloakOfSheep cloak = new CloakOfSheep();
		check(cloak.levelCap() == 10 && cloak.charge == 0, "绵羊披风初始等级上限或冷却错误");
		check(cloak.ready(), "初始状态应当已经就绪");
		check(cloak.image == pd.atlas.items.SpecificPlaceHolderDict.ARTIFACT_HOLDER_0,
				"绵羊披风占位图标错误");
		check(cloak.range() == CloakOfSheep.RANGE, "0 级闪烁距离应为 " + CloakOfSheep.RANGE + "：" + cloak.range());
		check(cloak.requireExp() == 2, "0 级升级门槛应为 2：" + cloak.requireExp());
		check(!cloak.actions(hero).contains(CloakOfSheep.AC_BLINK), "未装备时错误开放闪烁动作");

		hero.belongings.backpack.items.add(cloak);
		check(cloak.doEquip(hero), "绵羊披风无法装备");
		check(cloak.actions(hero).contains(CloakOfSheep.AC_BLINK), "就绪时没有开放闪烁动作");

		cloak.charge = CloakOfSheep.COOLDOWN;
		check(!cloak.ready(), "冷却中仍判定为就绪");
		check(!cloak.actions(hero).contains(CloakOfSheep.AC_BLINK), "冷却中仍开放闪烁动作");

		cloak.level(10);
		check(cloak.range() == CloakOfSheep.RANGE + 10 + CloakOfSheep.MAX_RANGE_BONUS,
				"满级闪烁距离错误：" + cloak.range());
	}

	private static void testCooldownAndDecoy() {
		Dungeon.level = new TestLevel();
		Hero hero = new Hero();
		hero.pos = 27;
		Dungeon.hero = hero;

		CloakOfSheep cloak = new CloakOfSheep();
		hero.belongings.backpack.items.add(cloak);
		check(cloak.doEquip(hero), "绵羊披风无法装备");
		cloak.activate(hero);
		cloak.charge = CloakOfSheep.COOLDOWN;

		CloakOfSheep.Recharge recharge = cloak.new Recharge();
		check(recharge.attachTo(hero), "绵羊披风冷却状态无法附加");
		for (int i = 0; i < CloakOfSheep.COOLDOWN - 1; i++) recharge.act();
		check(!cloak.ready(), "冷却未走完就判定为就绪：" + cloak.charge);
		recharge.act();
		check(cloak.ready(), "冷却走完后仍不可用：" + cloak.charge);

		//替身绵羊：寿命耗尽后自行消失
		DecoySheep sheep = new DecoySheep();
		check(sheep.alignment == pd.actors.Char.Alignment.NEUTRAL, "替身绵羊应为中立单位");
		sheep.initialize(3f);
		sheep.act();
		sheep.act();
		check(sheep.isAlive(), "替身绵羊过早消失：" + sheep.lifespan());
		sheep.act();
		check(!sheep.isAlive(), "替身绵羊寿命耗尽后没有消失");
	}

	private static void testPoolAndMessages() throws Exception {
		boolean inPool = false;
		for (Class<?> type : Generator.Category.ARTIFACT.classes) {
			if (type == CloakOfSheep.class) inPool = true;
		}
		check(inPool, "绵羊披风没有进入普通神器池");
		check(Generator.Category.ARTIFACT.classes.length == Generator.Category.ARTIFACT.defaultProbs.length,
				"神器池与权重数组长度不一致");

		for (String file : new String[]{"messages/items/zh/items.properties", "messages/items/en/items.properties"}) {
			String text = read(file);
			check(text.contains("items.equipment.artifacts.cloakofsheep.desc="), file + "缺少绵羊披风描述");
			check(text.contains("items.equipment.artifacts.cloakofsheep.ac_blink="), file + "缺少闪烁动作名");
		}
		for (String file : new String[]{"messages/actors/zh/actors.properties", "messages/actors/en/actors.properties"}) {
			String text = read(file);
			check(text.contains("actors.mobs.decoysheep.name="), file + "缺少替身绵羊名称");
		}
	}

	private static String read(String path) throws Exception {
		return new String(Files.readAllBytes(Path.of(path)), StandardCharsets.UTF_8);
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	/** 无头用的最小地图。 */
	private static final class TestLevel extends Level {
		TestLevel() {
			setSize(8, 8);
			mobs().clear();
			heaps = new SparseArray<>();
			blobs = new HashMap<>();
			plants = new SparseArray<Plant>();
			traps = new SparseArray<>();
			transitions = new ArrayList<>();
			customTiles = new ArrayList<>();
			customTerrain = new ArrayList<>();
			customWalls = new ArrayList<>();
			heroFOV = new boolean[length()];
			visited = new boolean[length()];
			mapped = new boolean[length()];
		}

		@Override protected boolean build() { return true; }
		@Override protected void createMobs() { }
		@Override protected void createItems() { }
		@Override public String tilesTex() { return null; }
		@Override public String waterTex() { return null; }
	}

	private SpsCloakOfSheepTest() { }
}
