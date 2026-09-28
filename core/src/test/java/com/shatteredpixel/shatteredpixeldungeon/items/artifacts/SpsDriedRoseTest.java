package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost;
import com.watabou.utils.Bundle;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Properties;

/** Runtime parity checks for SPS-PD 0.9.8's Dried Rose. */
public final class SpsDriedRoseTest {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		try {
			testQuestActionsAndSoulBless();
			testLegacyRecharge();
			testGhostVariants();
			testPersistence();
			testLocalizedResources();
			System.out.println("SPS干花玫瑰测试通过：任务动作、200点充能、超度、普通/领袖幽灵、存档和四语文本均符合0.9.8。");
		} finally {
			Ghost.Quest.reset();
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			app.exit();
		}
	}

	private static void testQuestActionsAndSoulBless() {
		RecordingHero hero = prepareHero();
		TestRose rose = new TestRose();
		check(DriedRose.AC_SUMMON.equals(rose.defaultAction()), "干花玫瑰默认动作不是旧版召唤");
		check(rose.chargeValue() == 200 && rose.chargeCapValue() == 200,
				"干花玫瑰初始充能或容量不是200");
		Ghost.Quest.reset();
		check(!rose.actions(hero).contains("EQUIP"), "幽灵任务完成前仍能装备干花玫瑰");

		completeGhostQuest();
		rose.identify(false);
		hero.belongings.artifact = rose;
		check(rose.actions(hero).contains(DriedRose.AC_SUMMON)
				&& rose.actions(hero).contains(DriedRose.AC_OUTFIT)
				&& !rose.actions(hero).contains(DriedRose.AC_DIRECT)
				&& !rose.actions(hero).contains(DriedRose.AC_SOULBLESS),
				"装备后的旧版召唤/配装动作或破碎版指引动作错误");

		hero.belongings.artifact = null;
		rose.level(5);
		check(rose.actions(hero).contains(DriedRose.AC_SOULBLESS), "未装备的有级玫瑰未显示耗竭-超度");
		rose.execute(hero, DriedRose.AC_SOULBLESS);
		check(hero.petLevel == 2 && hero.spent == 1f, "耗竭-超度没有按等级一半提升宠物或耗时一回合");
		check(rose.ghostStrength() == 30, "幽灵配装力量不是旧版固定30点");
	}

	private static void testLegacyRecharge() {
		RecordingHero hero = prepareHero();
		TestRose rose = new TestRose();
		hero.belongings.artifact = rose;
		rose.setCharge(0);
		DriedRose.roseRecharge recharge = rose.new roseRecharge();
		check(recharge.attachTo(hero), "玫瑰充能状态无法附加");
		for (int i = 0; i < 5; i++) recharge.act();
		check(rose.chargeValue() == 1 && close(rose.partialValue(), 1f),
				"玫瑰不是按旧版每回合0.4点部分充能");
		rose.charge(hero, 50f);
		check(rose.chargeValue() == 1, "破碎版外部神器供能仍会改变玫瑰充能");
		for (int i = 5; i < 501; i++) recharge.act();
		check(rose.chargeValue() == 200 && close(rose.partialValue(), 0f),
				"干花玫瑰没有按旧版每回合0.4的公式充满200点");
	}

	private static void testGhostVariants() {
		RecordingHero hero = prepareHero();
		TestRose rose = new TestRose();
		rose.level(4);
		hero.belongings.artifact = rose;

		DriedRose.GhostHero normal = new DriedRose.GhostHero(rose);
		check(normal.HT == 60 && normal.HP == 60, "普通幽灵生命不是20+10*玫瑰等级");
		check(normal.attackSkill(hero) == 10 && normal.defenseSkill(hero) == 5,
				"普通幽灵基础命中或闪避不是10/5");
		check(normal.decayDamage() == 1, "装备玫瑰时普通幽灵没有每回合损失1生命");
		for (int i = 0; i < 100; i++) {
			int damage = normal.damageRoll();
			check(damage >= 0 && damage <= 5, "普通幽灵徒手伤害越界");
		}

		hero.subClass = HeroSubClass.LEADER;
		DriedRose.SuperGhostHero leader = new DriedRose.SuperGhostHero(rose);
		check(leader.HT == 100 && leader.HP == 100, "领袖强化幽灵生命不是40+15*玫瑰等级");
		check(leader.attackSkill(hero) == 25 && leader.defenseSkill(hero) == 10,
				"领袖强化幽灵基础命中或闪避不是25/10");
		check(leader.decayDamage() == 0, "装备玫瑰时领袖强化幽灵仍在自行衰减");
		for (int i = 0; i < 100; i++) {
			int damage = leader.damageRoll();
			check(damage >= 0 && damage <= 10, "领袖强化幽灵徒手伤害越界");
		}

		hero.belongings.artifact = null;
		check(normal.decayDamage() == 5 && leader.decayDamage() == 5,
				"卸下玫瑰后两种幽灵没有每回合损失5生命");
	}

	private static void testPersistence() {
		TestRose source = new TestRose();
		source.level(7);
		source.setCharge(123);
		source.droppedPetals = 6;
		Bundle bundle = new Bundle();
		source.storeInBundle(bundle);
		TestRose restored = new TestRose();
		restored.restoreFromBundle(bundle);
		check(restored.level() == 7 && restored.chargeValue() == 123
				&& restored.chargeCapValue() == 200 && restored.droppedPetals == 6,
				"干花玫瑰等级、充能、容量或花瓣存档恢复错误");
	}

	private static void testLocalizedResources() throws Exception {
		for (String file : new String[]{"items.properties", "items_zh.properties",
				"items_zh-hant.properties", "items_ru.properties"}) {
			Properties items = load("messages/items/" + file);
			for (String key : new String[]{"name", "ac_summon", "ac_outfit", "ac_soulbless",
					"no_charge", "cursed", "no_space", "charged", "desc"}) {
				required(items, "items.artifacts.driedrose." + key, file);
			}
			required(items, "items.artifacts.driedrose$superghosthero.name", file);
			required(items, "items.artifacts.driedrose$superghosthero.desc", file);
		}
		Properties zh = load("messages/items/items_zh.properties");
		check("耗竭-超度".equals(zh.getProperty("items.artifacts.driedrose.ac_soulbless")),
				"干花玫瑰简体中文超度动作乱码或错误");
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
		hero.subClass = HeroSubClass.NONE;
		Dungeon.hero = hero;
		return hero;
	}

	private static void completeGhostQuest() {
		Bundle root = new Bundle();
		Bundle node = new Bundle();
		node.put("spawned", true);
		node.put("given", true);
		node.put("processed", true);
		root.put("sadGhost", node);
		Ghost.Quest.restoreFromBundle(root);
		check(Ghost.Quest.completed(), "测试无法建立已完成的幽灵任务状态");
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
		return Math.abs(actual - expected) < 0.0001f;
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private static final class RecordingHero extends Hero {
		float spent;
		@Override public void spend(float time) { spent += time; }
		@Override public void spendAndNext(float time) { spent += time; }
	}

	private static final class TestRose extends DriedRose {
		int chargeValue() { return charge; }
		int chargeCapValue() { return chargeCap; }
		float partialValue() { return partialCharge; }
		void setCharge(int value) { charge = value; }
	}

	private SpsDriedRoseTest() {
	}
}
