package pd.items.artifacts;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.buffs.MagicImmune;
import pd.actors.hero.Hero;
import pd.items.keys.IronKey;
import pd.scenes.InterlevelScene;
import com.watabou.utils.Bundle;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Properties;

/** Runtime parity checks for SPS-PD 0.9.8's Timekeeper's Hourglass. */
public final class SpsTimekeepersHourglassTest {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		try {
			testActionsAndRestart();
			testStasisAndFreeze();
			testRechargeAndSand();
			testSaveMigrationAndSources();
			testLocalizedResources();
			System.out.println("SPS时光沙漏测试通过：四回合停滞、四回合冻结耗能、耗竭重置、旧版充能、沙袋、存档和四语文本均符合0.9.8。");
		} finally {
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			app.exit();
		}
	}

	private static void testActionsAndRestart() {
		RecordingHero hero = prepareHero();
		TestHourglass hourglass = equip(hero);
		hourglass.setCharge(1);
		check(hourglass.actions(hero).contains(TimekeepersHourglass.AC_ACTIVATE),
				"一格充能时没有保留旧版可见但不可激活的动作");
		hourglass.level(5);
		hero.belongings.artifact = null;
		hero.belongings.backpack.items.add(hourglass);
		check(hourglass.actions(hero).contains(TimekeepersHourglass.AC_RESTART),
				"满级未装备沙漏没有显示耗竭重置");

		Dungeon.depth = 12;
		IronKey current = new IronKey(12);
		IronKey other = new IronKey(13);
		hero.belongings.backpack.items.add(current);
		hero.belongings.backpack.items.add(other);
		check(hourglass.restartNow(hero), "耗竭重置未执行");
		check(hourglass.level() == 0 && hourglass.capValue() == 5
				&& hourglass.chargeValue() <= hourglass.capValue() && hourglass.switched
				&& InterlevelScene.mode == InterlevelScene.Mode.RESET
				&& InterlevelScene.returnDepth == 12,
				"耗竭重置没有清零等级、同步容量或设置当前层重建");
		check(!hero.belongings.backpack.items.contains(current)
				&& hero.belongings.backpack.items.contains(other),
				"耗竭重置没有只移除当前层钥匙");
	}

	private static void testStasisAndFreeze() {
		RecordingHero hero = prepareHero();
		TestHourglass hourglass = equip(hero);
		hourglass.setCharge(5);
		hourglass.stasisNow(hero);
		check(hourglass.chargeValue() == 4 && close(hero.spent, 4f)
				&& hero.invisible == 1 && hero.buff(TimekeepersHourglass.timeStasis.class) != null,
				"停滞没有固定持续四回合、消耗一点或提供隐形无敌载体");
		hero.buff(TimekeepersHourglass.timeStasis.class).detach();
		check(hero.invisible == 0, "停滞结束后残留隐形");

		hourglass.setCharge(5);
		hourglass.freezeNow(hero);
		TimekeepersHourglass.timeFreeze freeze = hero.buff(TimekeepersHourglass.timeFreeze.class);
		check(freeze != null && hourglass.chargeValue() == 5,
				"冻结开始时错误预扣充能");
		freeze.processTime(3.9f);
		check(hourglass.chargeValue() == 5, "冻结不足四回合时过早耗能");
		freeze.processTime(0.1f);
		check(hourglass.chargeValue() == 4, "冻结满四回合没有消耗一点充能");
		freeze.detach();
		check(hourglass.chargeValue() == 3, "冻结结束时没有额外消耗一点充能");
		freeze.detach();
		check(hourglass.chargeValue() == 3,
				"冻结效果被重复结束时再次扣除充能");
	}

	private static void testRechargeAndSand() {
		RecordingHero hero = prepareHero();
		TestHourglass hourglass = equip(hero);
		hourglass.level(5);
		hourglass.setCap(10);
		hourglass.setCharge(0);
		TimekeepersHourglass.hourglassRecharge recharge = hourglass.new hourglassRecharge();
		check(recharge.attachTo(hero), "沙漏充能状态无法附加");
		for (int i = 0; i < 41; i++) recharge.act();
		check(hourglass.chargeValue() == 1 && hourglass.partialValue() < 0.05f,
				"空的满级沙漏没有按旧版约40回合一格公式充能");
		int beforeExternal = hourglass.chargeValue();
		hourglass.charge(hero, 100f);
		check(hourglass.chargeValue() == beforeExternal, "外部神器供能仍会增加沙漏充能");
		hourglass.setCursed(true);
		MagicImmune immunity = new MagicImmune();
		check(immunity.attachTo(hero) && recharge.isCursed(),
				"魔法免疫错误掩盖沙漏诅咒状态");

		TimekeepersHourglass.sandBag sand = new TimekeepersHourglass.sandBag();
		check(sand.value() == 10, "魔力流沙售价不是旧版10金币");
	}

	private static void testSaveMigrationAndSources() throws Exception {
		TestHourglass source = new TestHourglass();
		source.level(4);
		source.setCap(9);
		source.setCharge(8);
		source.setPartial(0.6f);
		source.sandBags = 4;
		Bundle saved = new Bundle();
		source.storeInBundle(saved);
		TestHourglass restored = new TestHourglass();
		restored.restoreFromBundle(saved);
		check(restored.level() == 4 && restored.capValue() == 9 && restored.chargeValue() == 8
				&& close(restored.partialValue(), 0.6f) && restored.sandBags == 4,
				"沙漏等级、容量、充能、部分充能或沙袋读档错误");

		TestHourglass migration = new TestHourglass();
		TimekeepersHourglass.timeFreeze freeze = migration.new timeFreeze();
		Bundle modern = new Bundle();
		modern.put("turnsToCost", 1f);
		freeze.restoreFromBundle(modern);
		check(close(migration.freezePartial(freeze), 2f),
				"早期SPS-SPD两回合冻结存档没有迁移到旧版四回合进度");

		String code = java.nio.file.Files.readString(Path.of(
				"../java/pd/items/artifacts/TimekeepersHourglass.java"));
		check(code.contains("1 / (60f - (chargeCap - charge) * 2f)")
				&& code.contains("partialTime >= 4f")
				&& !code.contains("RingOfEnergy.artifactChargeMultiplier")
				&& !code.contains("Talent.onArtifactUsed"),
				"沙漏仍残留破碎版正常流程的充能、冻结或天赋调用");
	}

	private static void testLocalizedResources() throws Exception {
		for (String file : new String[]{"items.properties", "items_zh.properties",
				"items_zh-hant.properties", "items_ru.properties"}) {
			Properties items = load("messages/items/" + file);
			for (String key : new String[]{"name", "ac_activate", "ac_restart", "in_use", "no_charge",
					"cursed", "onstasis", "onfreeze", "stasis", "freeze", "prompt", "desc",
					"desc_hint", "desc_cursed"}) {
				required(items, "items.artifacts.timekeepershourglass." + key, file);
			}
			for (String key : new String[]{"name", "levelup", "maxlevel", "no_hourglass", "desc"}) {
				required(items, "items.artifacts.timekeepershourglass$sandbag." + key, file);
			}
		}
		Properties zh = load("messages/items/items_zh.properties");
		check("耗竭-重置".equals(zh.getProperty("items.artifacts.timekeepershourglass.ac_restart"))
				&& !zh.getProperty("items.artifacts.timekeepershourglass.prompt").contains("两点能量"),
				"时光沙漏简体中文仍是破碎版说明或出现乱码");
	}

	private static RecordingHero prepareHero() {
		Actor.clear();
		RecordingHero hero = new RecordingHero();
		hero.HP = hero.HT = 100;
		Dungeon.hero = hero;
		Dungeon.level = null;
		return hero;
	}

	private static TestHourglass equip(RecordingHero hero) {
		TestHourglass hourglass = new TestHourglass();
		hero.belongings.artifact = hourglass;
		return hourglass;
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

	private static final class TestHourglass extends TimekeepersHourglass {
		boolean switched;
		int chargeValue() { return charge; }
		int capValue() { return chargeCap; }
		float partialValue() { return partialCharge; }
		void setCharge(int value) { charge = value; }
		void setCap(int value) { chargeCap = value; }
		void setPartial(float value) { partialCharge = value; }
		void setCursed(boolean value) { cursed = value; }
		void stasisNow(Hero hero) { beginStasis(hero); }
		void freezeNow(Hero hero) { beginFreeze(hero); }
		boolean restartNow(Hero hero) { return restartFloor(hero); }
		float freezePartial(TimekeepersHourglass.timeFreeze freeze) { return freeze.partialTime; }
		@Override protected void switchToResetScene() { switched = true; }
	}

	private SpsTimekeepersHourglassTest() { }
}
