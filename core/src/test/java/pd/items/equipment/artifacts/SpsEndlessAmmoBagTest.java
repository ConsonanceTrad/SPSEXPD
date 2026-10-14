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
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.Generator;
import pd.items.equipment.weapon.guns.GunA;
import pd.items.equipment.weapon.melee.Crossbow;
import render.utils.serialize.Bundle;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Headless checks for the endless ammo bag:
 * point-based charge, per-level cap growth, charge speed, the reload action and feeding upgrades.
 */
public final class SpsEndlessAmmoBagTest {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		try {
			testStats();
			testCapGrowth();
			testChargeSpeed();
			testReloadEquippedGuns();
			testFeedUpgrade();
			testPoolSaveAndMessages();
			System.out.println("SPS无限弹药袋测试通过：点数充能、上限随等级成长、充能速度、补充弹药与喂食升级、神器池与双语文本均正常。");
		} finally {
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			app.exit();
		}
	}

	private static void testStats() {
		Hero hero = new Hero();
		Dungeon.hero = hero;
		EndlessAmmoBag bag = new EndlessAmmoBag();
		check(bag.levelCap() == EndlessAmmoBag.MAX_LEVEL && bag.levelCap() == 10,
				"无限弹药袋等级上限错误：" + bag.levelCap());
		check(bag.chargeCap() == 4 && bag.charge() == 0, "无限弹药袋初始上限或充能错误");
		check(EndlessAmmoBag.AMMO_COST == 4 && EndlessAmmoBag.MAGAZINES_PER_LEVEL == 2,
				"无限弹药袋消耗或喂食常量错误");
		check(bag.image == SpecificPlaceHolderDict.ARTIFACT_HOLDER_0, "无限弹药袋占位图标错误");
		check(bag.status() == null || bag.status().isEmpty() || bag.status().contains("/"),
				"无限弹药袋状态文本异常");
	}

	private static void testCapGrowth() {
		check(EndlessAmmoBag.chargeCapForLevel(0) == 4, "0 级充能上限不是 4");
		check(EndlessAmmoBag.chargeCapForLevel(2) == 5, "2 级充能上限不是 5");
		check(EndlessAmmoBag.chargeCapForLevel(4) == 6, "4 级充能上限不是 6");
		check(EndlessAmmoBag.chargeCapForLevel(6) == 7, "6 级充能上限不是 7");
		check(EndlessAmmoBag.chargeCapForLevel(8) == 8, "8 级充能上限不是 8");
		check(EndlessAmmoBag.chargeCapForLevel(10) == 10, "10 级充能上限不是 10");

		EndlessAmmoBag bag = new EndlessAmmoBag();
		bag.upgrade(4);
		check(bag.chargeCap() == EndlessAmmoBag.chargeCapForLevel(4),
				"升级后上限没有随之成长：" + bag.chargeCap());
	}

	private static void testChargeSpeed() {
		EndlessAmmoBag bag = new EndlessAmmoBag();
		check(Math.abs(bag.turnsPerPoint() - 30f) < 0.001f, "0 级充能速度不是 30 回合/点");
		bag.upgrade(EndlessAmmoBag.MAX_LEVEL);
		check(Math.abs(bag.turnsPerPoint() - 12f) < 0.001f, "满级充能速度不是 12 回合/点");

		Hero hero = new Hero();
		Dungeon.hero = hero;
		EndlessAmmoBag fresh = new EndlessAmmoBag();
		hero.belongings.backpack.items.add(fresh);
		check(fresh.doEquip(hero), "无限弹药袋无法装备");
		fresh.activate(hero);
		EndlessAmmoBag.Feeding feeding = fresh.new Feeding();
		check(feeding.attachTo(hero), "无限弹药袋充能状态无法附加");
		for (int i = 0; i < 30; i++) feeding.act();
		check(fresh.charge() == 1, "0 级 30 回合没有积攒 1 点充能：" + fresh.charge());
		for (int i = 0; i < 300; i++) feeding.act();
		check(fresh.charge() == fresh.chargeCap(), "无限弹药袋充能没有封顶：" + fresh.charge());
	}

	private static void testReloadEquippedGuns() {
		Hero hero = new Hero();
		Dungeon.hero = hero;
		EndlessAmmoBag bag = new EndlessAmmoBag();
		hero.belongings.backpack.items.add(bag);
		check(bag.doEquip(hero), "无限弹药袋无法装备");
		bag.activate(hero);
		bag.addCharge(EndlessAmmoBag.AMMO_COST);

		GunA gun = new GunA();
		hero.belongings.weapon = gun;
		gun.charge(0);
		check(bag.reloadEquippedGuns(hero), "补充弹药没有补到常规枪械");
		check(gun.charge() == gun.fullCharge(), "常规枪械弹匣没有被补满：" + gun.charge());

		//特殊枪械（耗材特殊）不属于「常规枪械」，不该被补
		Crossbow crossbow = new Crossbow();
		crossbow.charge(0);
		hero.belongings.weapon = null;
		hero.belongings.secondWep = crossbow;
		check(!bag.reloadEquippedGuns(hero), "特殊枪械不该被补充弹药");
		check(crossbow.charge() == 0, "特殊枪械弹匣被错误补满：" + crossbow.charge());
		check(!crossbow.usesStandardAmmo() && gun.usesStandardAmmo(), "常规/特殊枪械判定错误");
	}

	private static void testFeedUpgrade() {
		Hero hero = new Hero();
		Dungeon.hero = hero;
		EndlessAmmoBag bag = new EndlessAmmoBag();
		hero.belongings.backpack.items.add(bag);
		check(bag.doEquip(hero), "无限弹药袋无法装备");
		bag.activate(hero);
		check(bag.level() == 0 && bag.chargeCap() == 4, "初始等级或上限错误");

		check(bag.feedMagazines(hero, 1), "喂食 1 个弹匣失败");
		check(bag.level() == 0, "喂食不足 2 个弹匣就升级了");
		check(bag.feedMagazines(hero, 1), "喂食第 2 个弹匣失败");
		check(bag.level() == 1, "每 2 个弹匣没有升 1 级：" + bag.level());
		check(bag.chargeCap() == EndlessAmmoBag.chargeCapForLevel(1), "升级后上限没有同步");

		bag.feedMagazines(hero, 2 * EndlessAmmoBag.MAX_LEVEL);
		check(bag.level() == EndlessAmmoBag.MAX_LEVEL, "喂食没有升到满级：" + bag.level());
		check(bag.chargeCap() == 10, "满级上限不是 10：" + bag.chargeCap());
	}

	private static void testPoolSaveAndMessages() throws Exception {
		//SPSEXPD: 无限弹药袋已移出常规神器池（改为星兵开局携带）
		boolean inPool = false;
		for (Class<?> type : Generator.Category.ARTIFACT.classes) {
			if (type == EndlessAmmoBag.class) inPool = true;
		}
		check(!inPool, "无限弹药袋不应再出现在常规神器池中");
		check(Generator.Category.ARTIFACT.classes.length == Generator.Category.ARTIFACT.defaultProbs.length,
				"神器池与权重数组长度不一致");

		EndlessAmmoBag bag = new EndlessAmmoBag();
		bag.addCharge(3);
		Bundle bundle = new Bundle();
		bag.storeInBundle(bundle);
		EndlessAmmoBag restored = new EndlessAmmoBag();
		restored.restoreFromBundle(bundle);
		check(restored.charge() == 3, "无限弹药袋充能没有随存档恢复：" + restored.charge());
		check(new EndlessAmmoBag().charge() == 0, "无限弹药袋充能没有实例隔离");

		String en = read("messages/items/en/items.properties");
		for (String key : new String[]{
				"items.equipment.artifacts.endlessammobag.desc=",
				"items.equipment.artifacts.endlessammobag.ac_activate=",
				"items.equipment.artifacts.endlessammobag.ac_feed=",
				"items.equipment.artifacts.endlessammobag.no_gun=",
				"items.equipment.artifacts.endlessammobag.level_up="}) {
			check(en.contains(key), "en 缺少无限弹药袋文本：" + key);
		}
		check(en.contains("items.equipment.artifacts.masterthievesarmband.steal_goods_ok="),
				"en 缺少魔术之手法杖的偷窃文案");
	}

	private static String read(String path) throws Exception {
		return new String(Files.readAllBytes(Path.of(path)), StandardCharsets.UTF_8);
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsEndlessAmmoBagTest() { }
}
