package pd.items.equipment.artifacts;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.buffs.GoldTouch;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.items.Item;
import pd.items.StoneOre;
import render.utils.serialize.Bundle;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Properties;

/** Runtime parity checks for SPS-PD 0.9.8's Master Thieves' Armband. */
public final class SpsMasterThievesArmbandTest {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		try {
			testActionsAndGoldTouch();
			testLootAndGrowth();
			testRecharge();
			testSaveCompatibility();
			testKillAndShopIntegration();
			testLocalizedResources();
			System.out.println("SPS神偷袖章测试通过：旧版盗取、点金、成长、击杀/时间充能、存档和四语文本均符合0.9.8。");
		} finally {
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			app.exit();
		}
	}

	private static void testActionsAndGoldTouch() {
		RecordingHero hero = prepareHero();
		TestArmband armband = equip(hero);
		check(armband.level() == 0 && armband.chargeCapValue() == 1 && armband.chargeValue() == 0,
				"神偷袖章初始等级、容量或充能错误");
		check(armband.status() == null, "未鉴定袖章错误显示了状态");
		armband.identify();
		check("0/1".equals(armband.status()), "已鉴定袖章状态不是旧版充能/容量");
		check(!armband.actions(hero).contains(MasterThievesArmband.AC_STEAL),
				"空充能袖章错误显示偷窃动作");
		armband.setCharge(1);
		check(armband.actions(hero).contains(MasterThievesArmband.AC_STEAL),
				"已装备且有充能的袖章未显示偷窃");

		hero.belongings.artifact = null;
		armband.level(3);
		check(armband.actions(hero).contains(MasterThievesArmband.AC_GOLDTOUCH),
				"二级以上未装备袖章未显示耗竭-点金");
		armband.goldTouch(hero);
		check(armband.level() == 2 && hero.spent == 1f, "点金没有降一级并耗时一回合");
		check(hero.buff(GoldTouch.class) != null && close(hero.buff(GoldTouch.class).cooldown(), 15f),
				"点金持续时间不是使用前等级乘5");
		armband.setCursed(true);
		check(!armband.actions(hero).contains(MasterThievesArmband.AC_GOLDTOUCH),
				"诅咒袖章错误显示点金动作");
	}

	private static void testLootAndGrowth() {
		RecordingHero hero = prepareHero();
		TestArmband armband = equip(hero);
		LootMob mob = new LootMob();
		Item first = armband.take(mob);
		Item second = armband.take(mob);
		check(first instanceof MarkerItem && second instanceof StoneOre && !mob.firstItem,
				"首次盗取未使用SupercreateLoot或重复盗取未改为石矿");

		NullLootMob nullMob = new NullLootMob();
		check(armband.take(nullMob) instanceof StoneOre && !nullMob.firstItem,
				"空专属掉落没有安全回退为石矿");

		armband.setCharge(10);
		armband.record();
		check(armband.level() == 1 && armband.expValue() == 0 && armband.chargeCapValue() == 2,
				"首次盗取没有按旧版从零级升到一级");
		armband.record();
		check(armband.level() == 2 && armband.expValue() == 0 && armband.chargeCapValue() == 3,
				"一级袖章没有在下一次盗取后升级");
		armband.record();
		check(armband.level() == 2 && armband.expValue() == 1, "二级袖章成长阈值错误");
		armband.record();
		check(armband.level() == 3 && armband.expValue() == 0 && armband.chargeValue() == 6,
				"盗取消耗或二级成长结果错误");
	}

	private static void testRecharge() {
		RecordingHero hero = prepareHero();
		TestArmband armband = equip(hero);
		MasterThievesArmband.Thievery thievery = armband.new Thievery();
		check(thievery.attachTo(hero), "神偷袖章充能状态无法附加");

		armband.setCharge(0);
		armband.setPartial(399f);
		thievery.act();
		check(armband.chargeValue() == 1 && close(armband.partialValue(), 0f),
				"时间充能不是严格每400回合一格");
		armband.setCharge(0);
		armband.setPartial(0f);
		armband.charge(hero, 100f);
		check(armband.chargeValue() == 0 && close(armband.partialValue(), 0f),
				"破碎版外部神器供能仍会增加袖章充能");

		armband.upgrade();
		armband.setPartial(399f);
		thievery.gainCharge();
		check(armband.chargeValue() == 0 && close(armband.partialValue(), 400f),
				"击杀充能错误地把等于400当作完成一格");
		thievery.gainCharge();
		check(armband.chargeValue() == 1 && close(armband.partialValue(), 0f),
				"击杀没有按当前袖章等级增加部分充能");

		armband.setCursed(true);
		armband.setCharge(0);
		armband.setPartial(20f);
		thievery.gainCharge();
		check(close(armband.partialValue(), 20f), "诅咒袖章错误获得了击杀充能");
	}

	private static void testSaveCompatibility() {
		TestArmband source = new TestArmband();
		for (int i = 0; i < 4; i++) source.upgrade();
		source.setCharge(5);
		source.setPartial(123f);
		Bundle saved = new Bundle();
		source.storeInBundle(saved);
		check(saved.contains("partialCharge"), "新存档没有保留0.9.8 partialCharge字段");

		TestArmband restored = new TestArmband();
		restored.restoreFromBundle(saved);
		check(restored.level() == 4 && restored.chargeCapValue() == 5
				&& restored.chargeValue() == 5 && close(restored.partialValue(), 123f),
				"升级袖章读档后等级、容量、充能或部分充能损坏");

		TestArmband modern = new TestArmband();
		modern.level(9);
		modern.setCharge(6);
		Bundle priorPort = new Bundle();
		modern.storeInBundle(priorPort);
		TestArmband migrated = new TestArmband();
		migrated.restoreFromBundle(priorPort);
		check(migrated.level() == 5 && migrated.chargeCapValue() == 6 && migrated.chargeValue() == 6,
				"早期SPS-SPD十级袖章存档没有安全迁移到旧版五级上限");
	}

	private static void testKillAndShopIntegration() throws Exception {
		String mob = java.nio.file.Files.readString(Path.of("../java/pd/actors/mobs/Mob.java"));
		String hero = java.nio.file.Files.readString(Path.of("../java/pd/actors/hero/Hero.java"));
		check(mob.contains("if (armband != null) armband.gainCharge();"),
				"怪物死亡流程没有接入旧版袖章击杀充能");
		check(!hero.contains("armband.gainCharge(percent)"),
				"英雄经验流程仍接入破碎版百分比充能");
		TestArmband armband = new TestArmband();
		MasterThievesArmband.Thievery thievery = armband.new Thievery();
		check(thievery.chargesToUse(new StoneOre()) == 0 && thievery.stealChance(new StoneOre()) == 0f,
				"破碎版商店偷窃动作仍会进入正常SPS流程");
	}

	private static void testLocalizedResources() throws Exception {
		for (String file : new String[]{"en/items.properties", "zh/items.properties",
				"zh-hant/items.properties", "ru/items.properties"}) {
			Properties items = load("messages/items/" + file);
			for (String key : new String[]{"name", "ac_steal", "ac_goldtouch", "no_charge",
					"cursed", "no_target", "level_up", "prompt", "desc", "desc_worn"}) {
				required(items, "items.artifacts.masterthievesarmband." + key, file);
			}
			for (Object value : items.values()) {
				check(!String.valueOf(value).contains("\uFFFD"), file + "包含Unicode替换字符");
			}
		}
		Properties zh = load("messages/items/zh/items.properties");
		check("耗竭-点金".equals(zh.getProperty("items.artifacts.masterthievesarmband.ac_goldtouch")),
				"神偷袖章简体中文点金动作乱码或错误");
	}

	private static RecordingHero prepareHero() {
		Actor.clear();
		RecordingHero hero = new RecordingHero();
		hero.HP = hero.HT = 100;
		Dungeon.hero = hero;
		return hero;
	}

	private static TestArmband equip(RecordingHero hero) {
		TestArmband armband = new TestArmband();
		hero.belongings.artifact = armband;
		return armband;
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

	private static final class TestArmband extends MasterThievesArmband {
		int chargeValue() { return charge; }
		int chargeCapValue() { return chargeCap; }
		int expValue() { return exp; }
		float partialValue() { return partialCharge; }
		void setCharge(int value) { charge = value; }
		void setPartial(float value) { partialCharge = value; }
		void setCursed(boolean value) { cursed = value; }
		void goldTouch(Hero hero) { applyGoldTouch(hero); }
		Item take(Mob mob) { return takeLegacyLoot(mob); }
		void record() { recordLegacySteal(); }
	}

	private static final class LootMob extends Mob {
		@Override public Item SupercreateLoot() { return new MarkerItem(); }
	}

	private static final class NullLootMob extends Mob {
		@Override public Item SupercreateLoot() { return null; }
	}

	private static final class MarkerItem extends Item { }

	private SpsMasterThievesArmbandTest() { }
}
