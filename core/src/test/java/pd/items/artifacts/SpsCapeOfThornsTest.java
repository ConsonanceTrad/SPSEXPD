package pd.items.artifacts;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.buffs.Needling;
import pd.actors.buffs.ShieldArmor;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.levels.features.HighGrass;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Properties;

/** Runtime parity checks for SPS-PD 0.9.8's Cape of Thorns. */
public final class SpsCapeOfThornsTest {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		try {
			testNeedlingAction();
			testChargeAndShield();
			testReflectionAndGrowth();
			testDetachAndSave();
			testHighGrassSeedBonus();
			testLocalizedResources();
			System.out.println("SPS荆棘斗篷测试通过：耗竭激发、旧版充能、护盾、远程反射、成长、存档、高草种子和四语文本均符合0.9.8。");
		} finally {
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			app.exit();
		}
	}

	private static void testNeedlingAction() {
		RecordingHero hero = prepareHero();
		TestCape cape = new TestCape();
		hero.belongings.artifact = cape;
		check(CapeOfThorns.AC_NEEDLING.equals(cape.defaultAction()), "荆棘斗篷默认动作不是耗竭激发");
		check(!cape.actions(hero).contains(CapeOfThorns.AC_NEEDLING), "零级斗篷错误显示耗竭激发");

		cape.level(2);
		check(cape.actions(hero).contains(CapeOfThorns.AC_NEEDLING), "二级斗篷未显示耗竭激发");
		cape.execute(hero, CapeOfThorns.AC_NEEDLING);
		Needling needling = hero.buff(Needling.class);
		check(cape.level() == 1 && needling != null && needling.cooldown() == 20f && hero.spent == 1f,
				"耗竭激发没有消耗一级、持续使用前等级乘10回合或耗时一回合");
		check(!cape.actions(hero).contains(CapeOfThorns.AC_NEEDLING), "一级斗篷仍显示耗竭激发");

		cape.level(3);
		cape.cursed = true;
		check(!cape.actions(hero).contains(CapeOfThorns.AC_NEEDLING), "诅咒斗篷仍显示耗竭激发");
	}

	private static void testChargeAndShield() {
		RecordingHero hero = prepareHero();
		TestCape cape = new TestCape();
		CapeOfThorns.Thorns thorns = cape.new Thorns();
		check(thorns.attachTo(hero), "荆棘状态无法附加到英雄");
		check(!thorns.toString().isEmpty(), "荆棘状态没有返回本地化名称");
		check(thorns.proc(10, null, hero) == 10 && cape.chargeValue() == 7,
				"零级斗篷没有按伤害乘0.7充能，或环境伤害空攻击者导致异常");
		cape.charge(hero, 20f);
		check(cape.chargeValue() == 7, "破碎版外部神器供能仍会给SPS斗篷充能");

		Actor.clear();
		hero = prepareHero();
		cape = new TestCape();
		cape.level(10);
		thorns = cape.new Thorns();
		check(thorns.attachTo(hero), "满级荆棘状态无法附加");
		thorns.proc(10, null, hero);
		check(cape.chargeValue() == 17, "十级斗篷没有按伤害乘1.7充能");

		Actor.clear();
		hero = prepareHero();
		cape = new TestCape();
		cape.level(5);
		cape.setCharge(95);
		thorns = cape.new Thorns();
		check(thorns.attachTo(hero), "满充测试的荆棘状态无法附加");
		thorns.proc(5, null, hero);
		ShieldArmor shield = hero.buff(ShieldArmor.class);
		check(cape.chargeValue() == 0 && cape.cooldownValue() == 15
				&& shield != null && shield.level() == 50,
				"斗篷满充没有清零、激活等级加10回合或给予等级乘10护盾");
	}

	private static void testReflectionAndGrowth() {
		RecordingHero hero = prepareHero();
		TestCape cape = new TestCape();
		cape.setCooldown(10);
		CapeOfThorns.Thorns thorns = cape.new Thorns();
		check(thorns.attachTo(hero), "反射测试的荆棘状态无法附加");
		RecordingMob attacker = new RecordingMob();
		attacker.pos = 999;
		hero.pos = 0;

		int totalDeflected = 0;
		Random.pushGenerator(0x53505354484F524EL);
		try {
			for (int i = 0; i < 8; i++) {
				int remaining = thorns.proc(10, attacker, hero);
				totalDeflected += 10 - remaining;
			}
		} finally {
			Random.popGenerator();
		}
		check(totalDeflected > 0 && attacker.reflectedDamage == totalDeflected,
				"激活斗篷没有把偏转伤害反射给远处攻击者");
		check(cape.level() > 0, "偏转伤害没有按(等级+1)乘5经验升级斗篷");

		cape.level(10);
		cape.setExp(1000);
		thorns.proc(10, null, hero);
		check(cape.level() == 10, "荆棘斗篷成长超过十级上限或空攻击者崩溃");
	}

	private static void testDetachAndSave() {
		RecordingHero hero = prepareHero();
		TestCape cape = new TestCape();
		cape.level(4);
		cape.setCharge(63);
		cape.setExp(17);
		Bundle saved = new Bundle();
		cape.storeInBundle(saved);
		TestCape restored = new TestCape();
		restored.restoreFromBundle(saved);
		check(restored.level() == 4 && restored.chargeValue() == 63 && restored.expValue() == 17,
				"斗篷等级、充能或成长经验没有写入存档");

		CapeOfThorns.Thorns thorns = cape.new Thorns();
		check(thorns.attachTo(hero), "卸下测试的荆棘状态无法附加");
		cape.setCooldown(9);
		thorns.detach();
		check(cape.chargeValue() == 0 && cape.cooldownValue() == 0,
				"卸下斗篷没有清空充能和激活时间");
	}

	private static void testHighGrassSeedBonus() {
		RecordingHero hero = prepareHero();
		TestCape cape = new TestCape();
		CapeOfThorns.Thorns thorns = cape.new Thorns();
		check(thorns.attachTo(hero), "高草测试的荆棘状态无法附加");
		check(HighGrass.spsThornsSeedDenominator(hero) == 0, "零级斗篷错误增加高草种子掉落");
		cape.level(1);
		check(HighGrass.spsThornsSeedDenominator(hero) == 15, "一级斗篷高草额外种子概率不是1/15");
		cape.level(10);
		check(HighGrass.spsThornsSeedDenominator(hero) == 6, "十级斗篷高草额外种子概率不是1/6");
		cape.cursed = true;
		check(HighGrass.spsThornsSeedDenominator(hero) == 0, "诅咒斗篷仍增加高草种子掉落");
	}

	private static void testLocalizedResources() throws Exception {
		for (String file : new String[]{"en/items.properties", "zh/items.properties",
				"zh-hant/items.properties", "ru/items.properties"}) {
			Properties items = load("messages/items/" + file);
			for (String key : new String[]{"name", "ac_needling", "desc", "desc_inactive", "desc_active"}) {
				required(items, "items.artifacts.capeofthorns." + key, file);
			}
			for (String key : new String[]{"inert", "radiating", "levelup", "name", "desc"}) {
				required(items, "items.artifacts.capeofthorns$thorns." + key, file);
			}
		}
		Properties zh = load("messages/items/zh/items.properties");
		Properties zhHant = load("messages/items/zh-hant/items.properties");
		check("耗竭-激发".equals(zh.getProperty("items.artifacts.capeofthorns.ac_needling")),
				"荆棘斗篷简体中文动作乱码或错误");
		check("耗竭-激發".equals(zhHant.getProperty("items.artifacts.capeofthorns.ac_needling")),
				"荆棘斗篷繁体中文动作乱码或错误");
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
		Dungeon.hero = hero;
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

	private static void required(Properties properties, String key, String file) {
		check(properties.getProperty(key) != null && !properties.getProperty(key).isEmpty(), file + "缺少文本：" + key);
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private static final class RecordingHero extends Hero {
		float spent;
		@Override public void spend(float time) { spent += time; }
	}

	private static final class RecordingMob extends Mob {
		int reflectedDamage;
		@Override public void damage(int damage, Object source) { reflectedDamage += damage; }
	}

	private static final class TestCape extends CapeOfThorns {
		int chargeValue() { return charge; }
		int cooldownValue() { return cooldown; }
		int expValue() { return exp; }
		void setCharge(int value) { charge = value; }
		void setCooldown(int value) { cooldown = value; }
		void setExp(int value) { exp = value; }
	}

	private SpsCapeOfThornsTest() {
	}
}
