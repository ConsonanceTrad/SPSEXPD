package pd.items.equipment.artifacts;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.buffs.Buff;
import pd.actors.buffs.EndlessAmmo;
import pd.actors.hero.Hero;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.Generator;
import pd.items.equipment.weapon.missiles.Thrower;
import pd.items.equipment.weapon.missiles.throwing.EscapeKnive;
import render.utils.serialize.Bundle;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Headless checks for the endless ammo bag: charge build-up, activation, upkeep and ammo immunity. */
public final class SpsEndlessAmmoBagTest {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		try {
			testStats();
			testCharging();
			testActivationAndUpkeep();
			testAmmoImmunity();
			testPoolSaveAndMessages();
			System.out.println("SPS无限弹药袋测试通过：充能、激活门槛、每回合消耗与自动关闭、弹药豁免、神器池、存档与双语文本均正常。");
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
		check(bag.levelCap() == 0 && bag.chargeCap() == 100 && bag.charge() == 0,
				"无限弹药袋初始等级上限、容量或充能错误");
		check(EndlessAmmoBag.DRAIN_PER_TURN == 3f && EndlessAmmoBag.ACTIVATE_COST == 20,
				"无限弹药袋维持消耗或激活门槛常量错误");
		check(bag.image == SpecificPlaceHolderDict.ARTIFACT_HOLDER_0, "无限弹药袋占位图标错误");
		check(bag.status() == null || bag.status().isEmpty() || bag.status().contains("/"),
				"无限弹药袋状态文本异常");
	}

	private static void testCharging() {
		Hero hero = new Hero();
		Dungeon.hero = hero;
		EndlessAmmoBag bag = new EndlessAmmoBag();
		hero.belongings.backpack.items.add(bag);
		check(bag.doEquip(hero), "无限弹药袋无法装备");
		bag.activate(hero);

		EndlessAmmoBag.Feeding feeding = bag.new Feeding();
		check(feeding.attachTo(hero), "无限弹药袋充能状态无法附加");
		for (int i = 0; i < 10; i++) feeding.act();
		check(bag.charge() == 20, "无限弹药袋每回合充能不是 2 点：" + bag.charge());

		for (int i = 0; i < 200; i++) feeding.act();
		check(bag.charge() == bag.chargeCap(), "无限弹药袋充能没有封顶：" + bag.charge());
	}

	private static void testActivationAndUpkeep() {
		EndlessAmmoBag bag = new EndlessAmmoBag();
		Hero hero = new Hero();
		Dungeon.hero = hero;
		hero.belongings.backpack.items.add(bag);
		check(bag.doEquip(hero), "无限弹药袋无法装备");
		bag.activate(hero);

		check(!bag.actions(hero).contains(EndlessAmmoBag.AC_ACTIVATE), "充能不足时错误开放激活动作");

		bag.addCharge(150);
		check(bag.charge() == bag.chargeCap(), "直接增减充能没有受容量限制：" + bag.charge());
		bag.addCharge(-40);
		check(bag.charge() == 60, "直接增减充能没有正确扣减：" + bag.charge());
		bag.consumeCharge(-5);
		check(bag.charge() == 60, "consumeCharge 收到负数不该增加充能");
		check(bag.actions(hero).contains(EndlessAmmoBag.AC_ACTIVATE), "充能足够时没有开放激活动作");

		//无头环境无法走 Item.execute（内部会调用 GameScene.cancel），直接附加激活状态
		Buff.affect(hero, EndlessAmmo.class);
		EndlessAmmo active = hero.buff(EndlessAmmo.class);
		check(active != null && EndlessAmmo.isActive(hero), "激活无限弹药袋没有生效");

		active.act();
		check(bag.charge() == 57, "激活状态每回合没有扣除充能：" + bag.charge());

		while (bag.charge() > 0) active.act();
		check(bag.charge() == 0 && hero.buff(EndlessAmmo.class) == null,
				"充能耗尽后无限弹药没有自动关闭");
		check(!EndlessAmmo.isActive(hero), "关闭后仍被视为弹药豁免中");
	}

	private static void testAmmoImmunity() {
		Hero hero = new Hero();
		Dungeon.hero = hero;
		Thrower thrower = new Thrower();
		EscapeKnive knives = new EscapeKnive(4);
		hero.belongings.backpack.items.add(knives);
		check(thrower.loadAmmoFromBackpack(hero, knives), "投掷器无法装填投掷物");

		EndlessAmmoBag bag = new EndlessAmmoBag();
		hero.belongings.backpack.items.add(bag);
		check(bag.doEquip(hero), "无限弹药袋无法装备");
		bag.activate(hero);
		Buff.affect(hero, EndlessAmmo.class);

		check(thrower.consumeAmmo() && thrower.loadedAmmo() != null && knives.quantity() == 4,
				"弹药豁免期间投掷器内仓仍被消耗");
		check(EndlessAmmo.isActive(hero), "弹药豁免状态意外结束");

		hero.buff(EndlessAmmo.class).detach();
		check(thrower.consumeAmmo() && knives.quantity() == 3,
				"弹药豁免结束后投掷器没有正常消耗内仓");
	}

	private static void testPoolSaveAndMessages() throws Exception {
		boolean inPool = false;
		for (Class<?> type : Generator.Category.ARTIFACT.classes) {
			if (type == EndlessAmmoBag.class) inPool = true;
		}
		check(inPool, "无限弹药袋没有进入普通神器池");
		check(Generator.Category.ARTIFACT.classes.length == Generator.Category.ARTIFACT.defaultProbs.length,
				"神器池与权重数组长度不一致");

		EndlessAmmoBag bag = new EndlessAmmoBag();
		bag.addCharge(75);
		Bundle bundle = new Bundle();
		bag.storeInBundle(bundle);
		EndlessAmmoBag restored = new EndlessAmmoBag();
		restored.restoreFromBundle(bundle);
		check(restored.charge() == 75, "无限弹药袋充能没有随存档恢复");
		check(new EndlessAmmoBag().charge() == 0, "无限弹药袋充能没有实例隔离");

		for (String file : new String[]{"messages/items/zh/items.properties", "messages/items/en/items.properties"}) {
			String text = read(file);
			check(text.contains("items.equipment.artifacts.endlessammobag.desc="), file + "缺少无限弹药袋描述");
			check(text.contains("items.equipment.artifacts.endlessammobag.ac_activate="), file + "缺少激活动作名");
			check(text.contains("items.equipment.artifacts.masterthievesarmband.steal_goods_ok="),
					file + "缺少魔术之手法杖的偷窃文案");
		}
		for (String file : new String[]{"messages/actors/zh/actors.properties", "messages/actors/en/actors.properties"}) {
			check(read(file).contains("actors.buffs.endlessammo.desc="), file + "缺少无限弹药状态描述");
		}
	}

	private static String read(String path) throws Exception {
		return new String(Files.readAllBytes(Path.of(path)), StandardCharsets.UTF_8);
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsEndlessAmmoBagTest() { }
}
