package pd.items.artifacts;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.buffs.ForeverShadow;
import pd.actors.buffs.Invisibility;
import pd.actors.hero.Hero;
import pd.ui.BuffIndicator;
import com.watabou.utils.Bundle;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Properties;

/** Runtime parity checks for SPS-PD 0.9.8's Cloak of Shadows. */
public final class SpsCloakOfShadowsTest {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		try {
			testActionsAndStealth();
			testRechargeAndGrowth();
			testForeverShadow();
			testDispelAndUnequip();
			testSaveMigration();
			testLocalizedResources();
			System.out.println("SPS暗影斗篷测试通过：旧版潜行、五回合耗能、时间充能、成长、耗竭永影、隐形驱散、存档迁移和四语文本均符合0.9.8。");
		} finally {
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			app.exit();
		}
	}

	private static void testActionsAndStealth() {
		RecordingHero hero = prepareHero();
		TestCloak cloak = equip(hero);
		check(CloakOfShadows.AC_STEALTH.equals(cloak.defaultAction())
				&& cloak.chargeValue() == 3 && cloak.chargeCapValue() == 3,
				"暗影斗篷默认动作、初始充能或容量不是0.9.8数值");
		check(!cloak.unique && cloak.value() > 0, "暗影斗篷仍是破碎版零价值唯一物品");
		check(cloak.actions(hero).contains(CloakOfShadows.AC_STEALTH)
				&& !cloak.actions(hero).contains(CloakOfShadows.AC_SHADOW), "零级斗篷动作错误");

		cloak.cursed = true;
		check(cloak.actions(hero).contains(CloakOfShadows.AC_STEALTH), "诅咒错误阻止了旧版普通潜行");
		cloak.cursed = false;
		cloak.execute(hero, CloakOfShadows.AC_STEALTH);
		CloakOfShadows.cloakStealth stealth = hero.buff(CloakOfShadows.cloakStealth.class);
		check(stealth != null && hero.invisible == 1 && hero.spent == 1f && cloak.stealthedValue(),
				"潜行没有耗时一回合、附加披风隐形或记录状态");

		stealth.act();
		check(cloak.chargeValue() == 2 && stealth.turnsToCost == 5,
				"潜行首次行动没有消耗一格或重置为五回合");
		for (int i = 0; i < 4; i++) stealth.act();
		check(cloak.chargeValue() == 2, "潜行在五回合前提前消耗充能");
		stealth.act();
		check(cloak.chargeValue() == 1, "潜行第五回合没有消耗下一格充能");

		cloak.execute(hero, CloakOfShadows.AC_STEALTH);
		check(hero.buff(CloakOfShadows.cloakStealth.class) == null && hero.invisible == 0
				&& hero.spent == 2f && !cloak.stealthedValue(), "手动取消潜行没有耗时或清除隐形");
		cloak.setCharge(1);
		check(!cloak.actions(hero).contains(CloakOfShadows.AC_STEALTH), "只剩一格时仍允许开始潜行");
	}

	private static void testRechargeAndGrowth() {
		RecordingHero hero = prepareHero();
		TestCloak cloak = equip(hero);
		cloak.setCharge(0);
		CloakOfShadows.cloakRecharge recharge = cloak.new cloakRecharge();
		check(recharge.attachTo(hero), "斗篷充能状态无法附加");
		recharge.act();
		check(close(cloak.partialValue(), 1f / 47f), "零级空斗篷充能速度不是每47回合一格");
		cloak.cursed = true;
		float before = cloak.partialValue();
		recharge.act();
		check(cloak.partialValue() > before, "诅咒斗篷被错误套用破碎版停止充能规则");
		cloak.charge(hero, 100f);
		check(cloak.chargeValue() == 0, "破碎版外部神器充能仍影响SPS斗篷");
		cloak.directCharge(2);
		check(cloak.chargeValue() == 2, "保留奖励的直接充能兼容入口失效");

		Actor.clear();
		hero = prepareHero();
		cloak = equip(hero);
		cloak.upgrade(10);
		cloak.setCharge(0);
		recharge = cloak.new cloakRecharge();
		check(recharge.attachTo(hero), "满级斗篷充能状态无法附加");
		recharge.act();
		check(cloak.chargeCapValue() == 10 && close(cloak.partialValue(), 1f / 30f),
				"十级斗篷容量或每30回合一格的充能速度错误");

		Actor.clear();
		hero = prepareHero();
		cloak = equip(hero);
		cloak.setCharge(10);
		CloakOfShadows.cloakStealth stealth = cloak.new cloakStealth();
		check(stealth.attachTo(hero), "成长测试的披风隐形无法附加");
		for (int i = 0; i < 21; i++) stealth.act();
		check(cloak.level() == 1 && cloak.expValue() == 0 && cloak.chargeCapValue() == 4,
				"斗篷没有按五次基础耗能升级、清经验或扩容");
	}

	private static void testForeverShadow() {
		RecordingHero hero = prepareHero();
		TestCloak cloak = equip(hero);
		cloak.level(4);
		check(cloak.actions(hero).contains(CloakOfShadows.AC_SHADOW), "四级斗篷未显示耗竭永影");
		cloak.execute(hero, CloakOfShadows.AC_SHADOW);
		ForeverShadow shadow = hero.buff(ForeverShadow.class);
		check(cloak.level() == 1 && shadow != null && shadow.cooldown() == 40f && hero.spent == 1f,
				"耗竭永影没有消耗三级、按使用前等级乘10持续或耗时一回合");
		check(shadow.icon() == BuffIndicator.BLESS && !shadow.toString().isEmpty(),
				"永影没有使用旧版祝福图标或本地化名称");
		check(Invisibility.DURATION == 15f, "通用隐形仍是破碎版20回合而非旧版15回合");
	}

	private static void testDispelAndUnequip() {
		RecordingHero hero = prepareHero();
		TestCloak cloak = equip(hero);
		cloak.execute(hero, CloakOfShadows.AC_STEALTH);
		CloakOfShadows.cloakStealth stealth = hero.buff(CloakOfShadows.cloakStealth.class);
		int before = cloak.chargeValue();
		Invisibility.dispel(hero);
		check(hero.buff(CloakOfShadows.cloakStealth.class) == null && hero.invisible == 0
				&& cloak.chargeValue() == before - 1, "攻击驱散没有执行旧版披风耗能或清除隐形");

		Actor.clear();
		hero = prepareHero();
		cloak = equip(hero);
		cloak.activate(hero);
		cloak.execute(hero, CloakOfShadows.AC_STEALTH);
		check(cloak.doUnequip(hero, false, false), "暗影斗篷无法卸下");
		check(hero.invisible == 0 && hero.buff(CloakOfShadows.cloakStealth.class) == null,
				"卸下潜行斗篷后残留永久隐形状态");
	}

	private static void testSaveMigration() {
		RecordingHero hero = prepareHero();
		TestCloak source = equip(hero);
		source.upgrade(4);
		source.setCharge(5);
		source.setExp(23);
		source.execute(hero, CloakOfShadows.AC_STEALTH);
		Bundle saved = new Bundle();
		source.storeInBundle(saved);
		check(saved.getBoolean("stealthed") && !saved.contains("buff"),
				"新斗篷存档没有使用0.9.8隐形布尔值或仍写破碎版嵌套状态");

		Actor.clear();
		hero = prepareHero();
		TestCloak restored = new TestCloak();
		restored.restoreFromBundle(saved);
		hero.belongings.artifact = restored;
		restored.activate(hero);
		check(restored.level() == 4 && restored.chargeValue() == 5 && restored.expValue() == 23
				&& hero.buff(CloakOfShadows.cloakStealth.class) != null,
				"旧版斗篷等级、充能、经验或潜行状态没有恢复");

		Bundle priorPort = new Bundle();
		TestCloak portSource = new TestCloak();
		portSource.storeInBundle(priorPort);
		priorPort.put("buff", new Bundle());
		TestCloak migratedPort = new TestCloak();
		migratedPort.restoreFromBundle(priorPort);
		check(migratedPort.stealthedValue(), "早期SPS-SPD嵌套披风状态没有迁移");

		Bundle legacy = new Bundle();
		TestCloak legacySource = new TestCloak();
		legacySource.upgrade(10);
		legacySource.setCharge(4);
		legacySource.setExp(99);
		legacySource.storeInBundle(legacy);
		legacy.put("cooldown", 1);
		TestCloak migratedLegacy = new TestCloak();
		migratedLegacy.restoreFromBundle(legacy);
		check(migratedLegacy.level() == 7 && migratedLegacy.chargeValue() == 10
				&& migratedLegacy.chargeCapValue() == 10 && migratedLegacy.expValue() == 0,
				"带cooldown的旧斗篷存档没有按0.7等级迁移并充满");
	}

	private static void testLocalizedResources() throws Exception {
		for (String file : new String[]{"items.properties", "items_zh.properties",
				"items_zh-hant.properties", "items_ru.properties"}) {
			Properties items = load("messages/items/" + file);
			for (String key : new String[]{"name", "ac_stealth", "ac_shadow", "cooldown", "no_charge", "desc"}) {
				required(items, "items.artifacts.cloakofshadows." + key, file);
			}
			for (String key : new String[]{"no_charge", "levelup", "name", "desc"}) {
				required(items, "items.artifacts.cloakofshadows$cloakstealth." + key, file);
			}
		}
		for (String file : new String[]{"actors.properties", "actors_zh.properties",
				"actors_zh-hant.properties", "actors_ru.properties"}) {
			Properties actors = load("messages/actors/" + file);
			required(actors, "actors.buffs.forevershadow.name", file);
			required(actors, "actors.buffs.forevershadow.desc", file);
		}
		Properties zh = load("messages/items/items_zh.properties");
		check("耗竭-永影".equals(zh.getProperty("items.artifacts.cloakofshadows.ac_shadow")),
				"暗影斗篷简体中文永影动作乱码或错误");
		try (java.util.stream.Stream<Path> paths = java.nio.file.Files.walk(Path.of("messages"))) {
			for (Path path : (Iterable<Path>) paths.filter(p -> p.toString().endsWith(".properties"))::iterator) {
				check(!java.nio.file.Files.readString(path, StandardCharsets.UTF_8).contains("\uFFFD"),
						"资源含替换字符：" + path);
			}
		}
	}

	private static RecordingHero prepareHero() {
		Actor.clear();
		Dungeon.challenges = 0;
		RecordingHero hero = new RecordingHero();
		hero.HP = hero.HT = 100;
		hero.lvl = 1;
		Dungeon.hero = hero;
		return hero;
	}

	private static TestCloak equip(RecordingHero hero) {
		TestCloak cloak = new TestCloak();
		hero.belongings.artifact = cloak;
		return cloak;
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
		check(properties.getProperty(key) != null && !properties.getProperty(key).isEmpty(), file + "缺少文本：" + key);
	}

	private static boolean close(float actual, float expected) {
		return Math.abs(actual - expected) < 0.00001f;
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private static final class RecordingHero extends Hero {
		float spent;
		@Override public void spend(float time) { spent += time; }
		@Override public void spendAndNext(float time) { spent += time; }
	}

	private static final class TestCloak extends CloakOfShadows {
		int chargeValue() { return charge; }
		int chargeCapValue() { return chargeCap; }
		int expValue() { return exp; }
		float partialValue() { return partialCharge; }
		boolean stealthedValue() {
			Bundle state = new Bundle();
			storeInBundle(state);
			return state.getBoolean("stealthed");
		}
		void setCharge(int value) { charge = value; }
		void setExp(int value) { exp = value; }
	}

	private SpsCloakOfShadowsTest() {
	}
}
