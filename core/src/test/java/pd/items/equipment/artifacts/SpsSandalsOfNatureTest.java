package pd.items.equipment.artifacts;

import pd.atlas.items.EquipmentJewelleryArtifactDict;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.buffs.MagicImmune;
import pd.actors.buffs.Roots;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.plants.Earthroot;
import pd.plants.Firebloom;
import pd.plants.Icecap;
import pd.plants.Sorrowmoss;
import render.utils.serialize.Bundle;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Properties;

/** Runtime parity checks for SPS-PD 0.9.8's Sandals of Nature. */
public final class SpsSandalsOfNatureTest {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		try {
			testActionsAndFeeding();
			testRootAndArmor();
			testNaturalismCharge();
			testSprout();
			testImagesAndSaveMigration();
			testIntegrationSources();
			testLocalizedResources();
			System.out.println("SPS自然凉鞋测试通过：十级喂种成长、扎根护甲、踩草充能、耗竭发芽、存档和四语文本均符合0.9.8。");
		} finally {
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			app.exit();
		}
	}

	private static void testActionsAndFeeding() {
		RecordingHero hero = prepareHero();
		TestSandals sandals = equip(hero);
		check(sandals.level() == 0 && sandals.chargeValue() == 0,
				"自然凉鞋初始等级或充能错误");
		check(sandals.actions(hero).contains(SandalsOfNature.AC_FEED)
				&& !sandals.actions(hero).contains(SandalsOfNature.AC_ROOT),
				"零级自然凉鞋动作错误");

		Earthroot.Seed earthroot = new Earthroot.Seed();
		sandals.feed(earthroot, hero);
		check(sandals.level() == 1 && sandals.seedCount() == 0 && hero.spent == 2f,
				"零级凉鞋没有在第一粒种子后升级或耗时错误");

		Firebloom.Seed fire = new Firebloom.Seed();
		sandals.feed(fire, hero);
		check(sandals.level() == 1 && sandals.seedCount() == 1, "一级凉鞋过早升级");
		float beforeDuplicate = hero.spent;
		sandals.feed(new Firebloom.Seed(), hero);
		check(sandals.level() == 1 && sandals.seedCount() == 1 && hero.spent == beforeDuplicate,
				"同一级重复种子没有被拒绝");
		sandals.feed(new Icecap.Seed(), hero);
		check(sandals.level() == 2 && sandals.seedCount() == 0 && hero.spent == 6f,
				"一级凉鞋没有在两种不同种子后升级");

		sandals.setCharge(4);
		check(sandals.actions(hero).contains(SandalsOfNature.AC_ROOT), "有充能凉鞋未显示扎根");
		sandals.setCursed(true);
		check(!sandals.actions(hero).contains(SandalsOfNature.AC_FEED)
				&& sandals.actions(hero).contains(SandalsOfNature.AC_ROOT),
				"诅咒凉鞋没有遵循旧版禁喂食但允许已有充能扎根");
		sandals.setCursed(false);
		hero.belongings.artifact = null;
		check(sandals.actions(hero).contains(SandalsOfNature.AC_SPROUT),
				"未装备的升级凉鞋未显示耗竭-发芽");
		sandals.level(10);
		hero.belongings.artifact = sandals;
		check(!sandals.actions(hero).contains(SandalsOfNature.AC_FEED), "满级凉鞋仍显示喂食");
	}

	private static void testRootAndArmor() {
		RecordingHero hero = prepareHero();
		TestSandals sandals = equip(hero);
		sandals.level(3);
		sandals.setCharge(9);
		sandals.rootNow(hero);
		check(sandals.chargeValue() == 0 && hero.spent == 0f, "扎根未清空充能或错误耗时");
		check(hero.buff(Roots.class) != null && close(hero.buff(Roots.class).cooldown(), 5f),
				"扎根没有施加五回合定身");
		Earthroot.MagicPlantArmor armor = hero.buff(Earthroot.MagicPlantArmor.class);
		check(armor != null && armor.level() == 9, "扎根护甲没有按全部充能设置耐久");
		check(armor.absorb(10) == 5 && armor.level() == 4,
				"植被护甲没有吸收50%伤害并扣除对应耐久");
		check(armor.absorb(10) == 6 && hero.buff(Earthroot.MagicPlantArmor.class) == null,
				"植被护甲耗尽时的剩余伤害或移除逻辑错误");

		Earthroot.MagicPlantArmor moving = new Earthroot.MagicPlantArmor();
		check(moving.attachTo(hero), "植被护甲无法附加");
		moving.level(5);
		hero.pos++;
		moving.act();
		check(hero.buff(Earthroot.MagicPlantArmor.class) == null, "英雄移动后植被护甲没有瓦解");
	}

	private static void testNaturalismCharge() {
		RecordingHero hero = prepareHero();
		hero.HT = hero.HP = 100;
		TestSandals sandals = equip(hero);
		SandalsOfNature.Naturalism naturalism = sandals.new Naturalism();
		check(naturalism.attachTo(hero), "自然亲和状态无法附加");
		naturalism.charge();
		check(sandals.chargeValue() == 1, "零级凉鞋首次踩草不是按生命差值的1%取整");
		sandals.level(4);
		sandals.setCharge(20);
		naturalism.charge();
		check(sandals.chargeValue() == 24, "四级凉鞋踩草不是按剩余差值的5%取整");
		sandals.charge(hero, 100f);
		check(sandals.chargeValue() == 24, "破碎版外部神器供能仍会增加凉鞋充能");
		sandals.setCursed(true);
		BuffHelper.addMagicImmune(hero);
		check(naturalism.isCursed(), "魔法免疫错误掩盖了旧版凉鞋诅咒状态");
	}

	private static void testSprout() {
		RecordingHero hero = prepareHero();
		TestSandals sandals = new TestSandals();
		sandals.level(3);
		hero.belongings.backpack.items.add(sandals);
		sandals.sproutNow(hero);
		check(sandals.seededCells == 7 && sandals.seededAmount == 7 * 120,
				"耗竭-发芽没有按全图每格40乘等级播撒旧版水化效果");
		check(hero.spent == 2f && !hero.belongings.backpack.items.contains(sandals),
				"耗竭-发芽没有耗时两回合并消耗神器");
	}

	private static void testImagesAndSaveMigration() {
		TestSandals images = new TestSandals();
		for (int level = 1; level <= 10; level++) {
			images.upgrade();
			int expected = level <= 3 ? EquipmentJewelleryArtifactDict.ARTIFACT_SANDALS
					: level <= 6 ? EquipmentJewelleryArtifactDict.ARTIFACT_SHOES
					: level <= 9 ? EquipmentJewelleryArtifactDict.ARTIFACT_BOOTS : EquipmentJewelleryArtifactDict.ARTIFACT_GREAVES;
			check(images.imageValue() == expected, "自然凉鞋在" + level + "级使用了错误形态");
		}

		TestSandals source = new TestSandals();
		source.level(7);
		source.setCharge(66);
		source.addSeedName("alpha");
		source.addSeedName("beta");
		Bundle saved = new Bundle();
		source.storeInBundle(saved);
		TestSandals restored = new TestSandals();
		restored.restoreFromBundle(saved);
		check(restored.level() == 7 && restored.chargeValue() == 66 && restored.seedCount() == 2
				&& restored.imageValue() == EquipmentJewelleryArtifactDict.ARTIFACT_BOOTS,
				"旧版凉鞋等级、充能、种子名称或形态读档错误");

		Bundle priorPort = new Bundle();
		TestSandals modern = new TestSandals();
		modern.level(3);
		modern.storeInBundle(priorPort);
		priorPort.put("seeds", new Class[]{Firebloom.Seed.class, Sorrowmoss.Seed.class});
		priorPort.put("cur_seed_effect", Firebloom.Seed.class);
		TestSandals migrated = new TestSandals();
		migrated.restoreFromBundle(priorPort);
		check(migrated.seedCount() == 2 && migrated.hasSeedName(new Firebloom.Seed().name())
				&& migrated.curSeedEffect == Firebloom.Seed.class,
				"早期SPS-SPD种子类型存档没有迁移为旧版名称列表");
	}

	private static void testIntegrationSources() throws Exception {
		String grass = java.nio.file.Files.readString(Path.of(
				"../java/pd/levels/features/HighGrass.java"));
		String hero = java.nio.file.Files.readString(Path.of(
				"../java/pd/actors/hero/Hero.java"));
		check(grass.contains("naturalismLevel = naturalism.itemLevel();")
				&& !grass.contains("naturalism.itemLevel() + 1"),
				"高草仍使用破碎版自然凉鞋等级加一规则");
		check(hero.contains("Earthroot.MagicPlantArmor naturalArmor")
				&& hero.contains("naturalArmor.absorb(damage)"),
				"英雄受击流程没有接入旧版50%植被护甲");
	}

	private static void testLocalizedResources() throws Exception {
		for (String file : new String[]{"en/items.properties", "zh/items.properties",
				"zh-hant/items.properties", "ru/items.properties"}) {
			Properties items = load("messages/items/" + file);
			for (String key : new String[]{"name", "ac_feed", "ac_root", "ac_sprout", "no_charge",
					"prompt", "already_fed", "levelup", "absorb_seed", "desc_0", "desc_1",
					"desc_2", "desc_3", "desc_hint", "desc_cursed", "desc_ability", "desc_seeds"}) {
				required(items, "items.artifacts.sandalsofnature." + key, file);
			}
		}
		for (String file : new String[]{"en/plants.properties", "zh/plants.properties",
				"zh-hant/plants.properties", "ru/plants.properties"}) {
			Properties plants = load("messages/plants/" + file);
			required(plants, "plants.earthroot$magicplantarmor.name", file);
			required(plants, "plants.earthroot$magicplantarmor.desc", file);
		}
		Properties zh = load("messages/items/zh/items.properties");
		check("耗竭-发芽".equals(zh.getProperty("items.artifacts.sandalsofnature.ac_sprout")),
				"自然凉鞋简体中文发芽动作乱码或错误");
	}

	private static RecordingHero prepareHero() {
		Actor.clear();
		RecordingHero hero = new RecordingHero();
		hero.HP = hero.HT = 100;
		Dungeon.hero = hero;
		return hero;
	}

	private static TestSandals equip(RecordingHero hero) {
		TestSandals sandals = new TestSandals();
		hero.belongings.artifact = sandals;
		return sandals;
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

	private static final class TestSandals extends SandalsOfNature {
		int seededCells;
		int seededAmount;
		int chargeValue() { return charge; }
		int imageValue() { return image; }
		int seedCount() { return seeds.size(); }
		void setCharge(int value) { charge = value; }
		void setCursed(boolean value) { cursed = value; }
		void rootNow(Hero hero) { root(hero); }
		void sproutNow(Hero hero) { sprout(hero); }
		void feed(Item seed, Hero hero) { feedSeed(seed, hero); }
		void addSeedName(String name) { seeds.add(name); }
		boolean hasSeedName(String name) { return seeds.contains(name); }
		@Override protected int sproutMapLength() { return 7; }
		@Override protected void addSproutWater(int cell, int amount) {
			seededCells++;
			seededAmount += amount;
		}
	}

	private static final class BuffHelper {
		static void addMagicImmune(Hero hero) {
			MagicImmune immune = new MagicImmune();
			immune.attachTo(hero);
		}
	}

	private SpsSandalsOfNatureTest() { }
}
