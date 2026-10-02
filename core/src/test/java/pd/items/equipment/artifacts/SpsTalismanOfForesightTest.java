package pd.items.equipment.artifacts;

import pd.atlas.items.SpecificPlaceHolderDict;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.buffs.Awareness;
import pd.actors.buffs.MagicImmune;
import pd.actors.buffs.Notice;
import pd.actors.hero.Hero;
import pd.actors.mobs.npcs.SpsSokobanSheep;
import pd.items.Heap;
import pd.levels.Level;
import pd.levels.SpsSokobanLevel;
import pd.levels.Terrain;
import pd.levels.traps.Trap;
import pd.plants.Plant;
import pd.ui.BuffIndicator;
import render.utils.data.SparseArray;
import render.utils.serialize.Bundle;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import pd.atlas.IconEntry;

/** Runtime parity checks for SPS-PD 0.9.8's Talisman of Foresight. */
public final class SpsTalismanOfForesightTest {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		try {
			testActionsAndSokobanRestriction();
			testScryAndNotice();
			testTimeAndSecretCharging();
			testWarningAndCurse();
			testSaveAndIntegration();
			testLocalizedResources();
			System.out.println("SPS先见护符测试通过：满充探查、耗竭预知、危险提示、时间/秘密成长、存档和四语文本均符合0.9.8。");
		} finally {
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			app.exit();
		}
	}

	private static void testActionsAndSokobanRestriction() {
		RecordingHero hero = prepareHero(new TestLevel());
		TestTalisman talisman = equip(hero);
		check(talisman.imageValue() == SpecificPlaceHolderDict.SOMETHING_0
				&& talisman.level() == 0 && talisman.chargeValue() == 0,
				"先见护符初始图标、等级或充能错误");
		check(!talisman.actions(hero).contains(TalismanOfForesight.AC_SCRY),
				"未满充护符错误显示探查");

		talisman.level(3);
		talisman.setCharge(100);
		check(talisman.actions(hero).contains(TalismanOfForesight.AC_SCRY)
				&& talisman.actions(hero).contains(TalismanOfForesight.AC_NOTICE),
				"满充三级护符没有显示旧版两个动作");
		Dungeon.level = new TestSokobanLevel();
		check(!talisman.actions(hero).contains(TalismanOfForesight.AC_SCRY)
				&& talisman.actions(hero).contains(TalismanOfForesight.AC_NOTICE),
				"推箱子关没有只禁用探查");
		Dungeon.level = new TestLevel();
		talisman.setCursed(true);
		check(!talisman.actions(hero).contains(TalismanOfForesight.AC_SCRY)
				&& !talisman.actions(hero).contains(TalismanOfForesight.AC_NOTICE),
				"诅咒护符仍显示主动动作");
	}

	private static void testScryAndNotice() {
		TestLevel level = new TestLevel();
		RecordingHero hero = prepareHero(level);
		TestTalisman talisman = equip(hero);
		level.setSecretTrap(hero.pos + 1, true);
		talisman.level(5);
		talisman.setCharge(100);
		talisman.scryNow(hero);
		check(talisman.chargeValue() == 0, "探查没有清空100点充能");
		check(hero.buff(Awareness.class) != null
				&& close(hero.buff(Awareness.class).cooldown(), Awareness.DURATION),
				"探查没有施加旧版两回合全图物品感知");

		talisman.noticeNow(hero);
		Notice notice = hero.buff(Notice.class);
		check(talisman.level() == 3 && notice != null && close(notice.cooldown(), 50f),
				"耗竭预知没有按使用前等级乘十回合并降低两级");
		check(close(hero.spent, 1f), "耗竭预知耗时不是一回合");
	}

	private static void testTimeAndSecretCharging() {
		RecordingHero hero = prepareHero(new TestLevel());
		TestTalisman talisman = equip(hero);
		TalismanOfForesight.Foresight foresight = talisman.new Foresight();
		check(foresight.attachTo(hero), "先见状态无法附加");
		for (int i = 0; i < 24; i++) foresight.act();
		check(talisman.chargeValue() == 0 && talisman.partialValue() > 0.95f,
				"零级护符在旧版时间公式前24回合过早充能");
		foresight.act();
		foresight.act();
		check(talisman.chargeValue() == 1 && talisman.partialValue() < 0.09f,
				"零级护符没有按旧版浮点公式获得第一点充能");
		int beforeExternal = talisman.chargeValue();
		talisman.charge(hero, 50f);
		check(talisman.chargeValue() == beforeExternal, "破碎版外部神器供能仍会增加护符充能");

		talisman.setCharge(0);
		talisman.setPartial(0);
		for (int i = 0; i < 4; i++) foresight.charge();
		check(talisman.level() == 1 && talisman.expValue() == 0 && talisman.chargeValue() == 8,
				"护符没有每发现四个秘密升级，或秘密充能公式错误");
		talisman.level(6);
		talisman.setCharge(0);
		foresight.charge();
		check(talisman.chargeValue() == 4, "六级护符每个秘密应恢复2+等级除以3点充能");
	}

	private static void testWarningAndCurse() {
		TestLevel level = new TestLevel();
		RecordingHero hero = prepareHero(level);
		TestTalisman talisman = equip(hero);
		TalismanOfForesight.Foresight foresight = talisman.new Foresight();
		check(foresight.attachTo(hero), "危险提示状态无法附加");
		level.setSecretTrap(hero.pos + 1, true);
		foresight.checkAwareness();
		check(foresight.icon() == BuffIndicator.FORESIGHT, "三格内可见秘密没有触发危险提示");
		level.setSecretTrap(hero.pos + 1, false);
		foresight.checkAwareness();
		foresight.checkAwareness();
		check(foresight.icon() == BuffIndicator.FORESIGHT, "旧版三回合提示过早消失");
		foresight.checkAwareness();
		check(foresight.icon() == BuffIndicator.NONE, "旧版危险提示超过三回合仍未消失");

		talisman.setCursed(true);
		MagicImmune immunity = new MagicImmune();
		check(immunity.attachTo(hero) && foresight.isCursed(),
				"魔法免疫错误掩盖了旧版诅咒护符的搜索惩罚");
	}

	private static void testSaveAndIntegration() throws Exception {
		TestTalisman source = new TestTalisman();
		source.level(7);
		source.setCharge(63);
		source.setPartial(0.75f);
		source.setExp(3);
		Bundle bundle = new Bundle();
		source.storeInBundle(bundle);
		TestTalisman restored = new TestTalisman();
		restored.restoreFromBundle(bundle);
		check(restored.level() == 7 && restored.chargeValue() == 63
				&& close(restored.partialValue(), 0.75f) && restored.expValue() == 3,
				"护符等级、充能、部分充能或成长读档错误");

		String hero = java.nio.file.Files.readString(Path.of(
				"../java/pd/actors/hero/Hero.java"));
		String talisman = java.nio.file.Files.readString(Path.of(
				"../java/pd/items/artifacts/TalismanOfForesight.java"));
		check(hero.contains("!talisman.isCursed()) talisman.charge();")
				&& !hero.contains("talisman.charge(2)") && !hero.contains("talisman.charge(10)"),
				"英雄搜索仍使用破碎版按秘密类型充能");
		check(talisman.contains("charge == chargeCap")
				&& talisman.contains("partialCharge > 1f")
				&& !talisman.contains("RingOfEnergy.artifactChargeMultiplier"),
				"护符仍残留破碎版正常流程的动作或充能公式");
	}

	private static void testLocalizedResources() throws Exception {
		for (String file : new String[]{"en/items.properties", "zh/items.properties",
				"zh-hant/items.properties", "ru/items.properties"}) {
			Properties items = load("messages/items/" + file);
			for (String key : new String[]{"name", "ac_scry", "ac_notice", "no_charge", "scry",
					"desc", "desc_worn", "desc_cursed"}) {
				required(items, "items.equipment.artifacts.talismanofforesight." + key, file);
			}
			for (String key : new String[]{"name", "levelup", "full_charge", "uneasy", "desc"}) {
				required(items, "items.equipment.artifacts.talismanofforesight$foresight." + key, file);
			}
		}
		Properties zh = load("messages/items/zh/items.properties");
		check("耗竭-预知".equals(zh.getProperty("items.equipment.artifacts.talismanofforesight.ac_notice"))
				&& "护符将关于本层的知识填满了你的脑海。".equals(
				zh.getProperty("items.equipment.artifacts.talismanofforesight.scry")),
				"先见护符简体中文旧版动作或提示乱码");
	}

	private static RecordingHero prepareHero(Level level) {
		Actor.clear();
		RecordingHero hero = new RecordingHero();
		hero.HT = hero.HP = 100;
		hero.pos = level.width() + 1;
		Dungeon.hero = hero;
		Dungeon.level = level;
		return hero;
	}

	private static TestTalisman equip(RecordingHero hero) {
		TestTalisman talisman = new TestTalisman();
		hero.belongings.artifact = talisman;
		return talisman;
	}

	private static Properties load(String path) throws Exception {
		Properties properties = new Properties();
		try (InputStreamReader reader = new InputStreamReader(
				java.nio.file.Files.newInputStream(Path.of(path)), StandardCharsets.UTF_8)) {
			properties.load(reader);
		}
		return properties;
	}

	private static void required(Properties properties, String key, String file) {
		check(properties.getProperty(key) != null && !properties.getProperty(key).isEmpty(),
				file + "缺少文本：" + key);
	}

	private static boolean close(float actual, float expected) { return Math.abs(actual - expected) < 0.0001f; }
	private static void check(boolean condition, String message) { if (!condition) throw new AssertionError(message); }

	private static final class RecordingHero extends Hero {
		float spent;
		@Override public void spend(float time) { spent += time; }
		@Override public void spendAndNext(float time) { spent += time; }
	}

	private static final class TestTalisman extends TalismanOfForesight {
		int chargeValue() { return charge; }
		float partialValue() { return partialCharge; }
		int expValue() { return exp; }
		IconEntry imageValue() { return image; }
		void setCharge(int value) { charge = value; }
		void setPartial(float value) { partialCharge = value; }
		void setExp(int value) { exp = value; }
		void setCursed(boolean value) { cursed = value; }
		void scryNow(Hero hero) { useScry(hero); }
		void noticeNow(Hero hero) { useNotice(hero); }
	}

	private static class TestLevel extends Level {
		TestLevel() {
			setSize(9, 9);
			mobs().clear(); heaps = new SparseArray<Heap>(); blobs = new HashMap<>();
			plants = new SparseArray<Plant>(); traps = new SparseArray<Trap>(); transitions = new ArrayList<>();
			customTiles = new ArrayList<>(); customTerrain = new ArrayList<>(); customWalls = new ArrayList<>();
			Arrays.fill(map, Terrain.EMPTY); Arrays.fill(passable, true); buildFlagMaps();
			Arrays.fill(heroFOV, true);
		}
		void setSecretTrap(int cell, boolean present) {
			map[cell] = present ? Terrain.SECRET_TRAP : Terrain.EMPTY;
			buildFlagMaps();
			Arrays.fill(heroFOV, true);
		}
		@Override protected boolean build() { return true; }
		@Override protected void createMobs() { }
		@Override protected void createItems() { }
		@Override public String tilesTex() { return null; }
		@Override public String waterTex() { return null; }
	}

	private static final class TestSokobanLevel extends TestLevel implements SpsSokobanLevel {
		@Override public void afterSheepMoved(SpsSokobanSheep sheep) { }
		@Override public int randomFleecingCell(int start, int maxDistance) { return start; }
		@Override public void resetPuzzle(Hero hero) { }
	}

	private SpsTalismanOfForesightTest() { }
}
