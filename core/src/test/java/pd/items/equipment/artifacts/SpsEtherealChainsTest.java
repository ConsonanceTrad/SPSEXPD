package pd.items.equipment.artifacts;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.buffs.AttackDown;
import pd.actors.buffs.Locked;
import pd.actors.buffs.Silent;
import pd.actors.buffs.Slow;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Properties;

/** Runtime parity checks for SPS-PD 0.9.8's Ethereal Chains. */
public final class SpsEtherealChainsTest {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		try {
			testActionsAndLock();
			testRechargeAndGrowth();
			testLocalizedResources();
			System.out.println("SPS虚空锁链测试通过：旧版动作、耗竭封印、时间充能、击杀成长和四语文本均符合0.9.8。");
		} finally {
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			app.exit();
		}
	}

	private static void testActionsAndLock() {
		RecordingHero hero = prepareHero();
		TestChains chains = equip(hero);
		check(chains.chargeValue() == 5 && chains.level() == 0, "虚空锁链初始充能或等级错误");
		check(chains.actions(hero).contains(EtherealChains.AC_CAST)
				&& !chains.actions(hero).contains(EtherealChains.AC_LOCKED), "零级锁链动作错误");
		chains.level(3);
		check(chains.actions(hero).contains(EtherealChains.AC_LOCKED), "三级锁链未显示耗竭-封印");

		Mob target = new TestMob();
		target.HP = target.HT = 100;
		chains.lock(hero, target);
		check(chains.level() == 2 && hero.spent == 1f, "耗竭-封印没有降一级并耗时一回合");
		check(target.buff(Locked.class) != null && close(target.buff(Locked.class).cooldown(), 12f),
				"封印没有按使用前等级乘4施加锁定");
		check(target.buff(Silent.class) != null && target.buff(Slow.class) != null,
				"封印缺少沉默或减速");
		check(target.buff(AttackDown.class) != null && target.buff(AttackDown.class).level() == 90,
				"封印没有施加90%攻击削弱");
	}

	private static void testRechargeAndGrowth() {
		RecordingHero hero = prepareHero();
		TestChains chains = equip(hero);
		chains.setCharge(0);
		EtherealChains.chainsRecharge recharge = chains.new chainsRecharge();
		check(recharge.attachTo(hero), "锁链充能状态无法附加");
		recharge.act();
		check(close(chains.partialValue(), 1f/30f), "零充能锁链不是按旧版每30回合一格恢复");
		chains.charge(hero, 100f);
		check(chains.chargeValue() == 0, "破碎版外部神器供能仍会增加锁链充能");

		chains.setPartial(0f);
		recharge.gainExp(1.01f);
		check(chains.level() == 1 && chains.expValue() == 1 && close(chains.partialValue(), 10.1f),
				"锁链没有按100+等级*50阈值升级或按击杀比例*10补充充能");
	}

	private static void testLocalizedResources() throws Exception {
		for (String file : new String[]{"en/items.properties", "zh/items.properties",
				"zh-hant/items.properties", "ru/items.properties"}) {
			Properties items = load("messages/items/" + file);
			for (String key : new String[]{"name", "ac_cast", "ac_locked", "no_charge", "cursed", "prompt", "desc"}) {
				required(items, "items.equipment.artifacts.etherealchains." + key, file);
			}
		}
		Properties zh = load("messages/items/zh/items.properties");
		check("耗竭-封印".equals(zh.getProperty("items.equipment.artifacts.etherealchains.ac_locked")),
				"虚空锁链简体中文封印动作乱码或错误");
	}

	private static RecordingHero prepareHero() {
		Actor.clear();
		RecordingHero hero = new RecordingHero();
		hero.HP = hero.HT = 100;
		Dungeon.hero = hero;
		return hero;
	}

	private static TestChains equip(RecordingHero hero) {
		TestChains chains = new TestChains();
		hero.belongings.artifact = chains;
		return chains;
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

	private static boolean close(float actual, float expected) { return Math.abs(actual - expected) < 0.0001f; }
	private static void check(boolean condition, String message) { if (!condition) throw new AssertionError(message); }

	private static final class RecordingHero extends Hero {
		float spent;
		@Override public void spend(float time) { spent += time; }
		@Override public void spendAndNext(float time) { spent += time; }
	}

	private static final class TestChains extends EtherealChains {
		int chargeValue() { return charge; }
		int expValue() { return exp; }
		float partialValue() { return partialCharge; }
		void setCharge(int value) { charge = value; }
		void setPartial(float value) { partialCharge = value; }
		void lock(Hero hero, Mob target) { curUser = hero; applyLegacyLock(target); }
	}

	private static final class TestMob extends Mob { }

	private SpsEtherealChainsTest() { }
}
