package pd.items.equipment.weapon.missiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.Item;
import pd.items.equipment.weapon.missiles.throwing.EscapeKnive;
import pd.items.equipment.weapon.spammo.FireAmmo;
import render.noosa.Game;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Headless checks for the original Thrower: loading, range limits and ammo consumption. */
public final class SpsThrowerTest {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Random.pushGenerator(0x5350535448524F57L);
		try {
			testStats();
			testAmmoSelection();
			testLoading();
			testConsumption();
			testSaving();
			testMessages();
			System.out.println("SPS投掷器测试通过：数值、动作、装填、射程限制、消耗、存档与双语文本均正常。");
		} finally {
			Random.popGenerator();
			Dungeon.hero = null;
			Dungeon.level = null;
		}
	}

	private static void testStats() {
		Hero hero = new Hero();
		Dungeon.hero = hero;
		Thrower thrower = new Thrower();
		check(thrower.min(0) == 4 && thrower.max(0) == 10
						&& thrower.min(2) == 6 && thrower.max(2) == 14 && thrower.STRReq(0) == 13,
				"投掷器数值或力量需求错误");
		check(thrower.image == SpecificPlaceHolderDict.SPS_PH_WEAPON, "投掷器占位图标错误");
		check(thrower.isUpgradable() && thrower.isIdentified(), "投掷器升级或鉴定属性错误");
		check(Thrower.BASE_RANGE == 3 && Math.abs(Thrower.LOAD_TIME - 2f) < 0.00001f,
				"投掷器射程或装填耗时错误");
		check(!thrower.isLoaded() && thrower.rangeLimit() == Thrower.BASE_RANGE,
				"未装填时射程限制错误");
		check(thrower.actions(hero).contains(Thrower.AC_SHOOT)
				&& thrower.actions(hero).contains(Thrower.AC_LOAD),
				"投掷器缺少射击或装填动作");
	}

	private static void testAmmoSelection() {
		check(Thrower.isValidAmmo(new EscapeKnive(1)), "普通投掷物应可作为装填物");
		check(!Thrower.isValidAmmo(new FireAmmo()), "涂油弹药不应作为装填物");
		check(!Thrower.isValidAmmo(new Item()), "非投掷物不应作为装填物");
		check(!Thrower.isValidAmmo(new ManyKnive().new KniveAmmo()), "无限临时投掷物不应作为装填物");
	}

	private static void testLoading() {
		Hero hero = new Hero();
		Dungeon.hero = hero;
		Thrower thrower = new Thrower();
		EscapeKnive knives = new EscapeKnive(4);
		hero.belongings.backpack.items.add(knives);

		check(thrower.loadAmmoFromBackpack(hero, knives), "投掷器无法装填背包中的投掷物");
		check(thrower.isLoaded() && thrower.loadedAmmo() == knives && knives.quantity() == 4,
				"投掷器装填后状态错误");
		check(!hero.belongings.backpack.contains(knives), "装填后背包仍保留投掷物");
		check(thrower.rangeLimit() == Integer.MAX_VALUE, "装填后射程没有变为无限");

		EscapeKnive more = new EscapeKnive(1);
		hero.belongings.backpack.items.add(more);
		check(!thrower.loadAmmoFromBackpack(hero, more) && hero.belongings.backpack.contains(more),
				"已装填时不应再次装填");

		Thrower empty = new Thrower();
		check(!empty.loadAmmoFromBackpack(hero, new FireAmmo()), "投掷器不应装填非投掷物");
	}

	private static void testConsumption() {
		Hero hero = new Hero();
		Dungeon.hero = hero;
		Thrower thrower = new Thrower();
		EscapeKnive knives = new EscapeKnive(4);
		hero.belongings.backpack.items.add(knives);
		thrower.loadAmmoFromBackpack(hero, knives);

		check(thrower.consumeAmmo() && thrower.isLoaded() && knives.quantity() == 3,
				"射出后没有消耗一件投掷物");
		for (int i = 0; i < 3; i++) thrower.consumeAmmo();
		check(!thrower.isLoaded() && thrower.rangeLimit() == Thrower.BASE_RANGE,
				"投掷物耗尽后内仓没有清空");
		check(!thrower.consumeAmmo(), "空仓不应被消耗");
	}

	private static void testSaving() {
		Thrower thrower = new Thrower();
		EscapeKnive knives = new EscapeKnive(3);
		Bundle bundle = new Bundle();
		thrower.storeInBundle(bundle);
		bundle.put("loaded", knives);

		Thrower restored = new Thrower();
		restored.restoreFromBundle(bundle);
		check(restored.isLoaded() && restored.loadedAmmo() instanceof EscapeKnive
						&& restored.loadedAmmo().quantity() == 3,
				"装填状态没有随存档恢复");
		check(!new Thrower().isLoaded(), "装填状态没有实例隔离");
	}

	private static void testMessages() throws Exception {
		for (String file : new String[]{"messages/items/zh/items.properties", "messages/items/en/items.properties"}) {
			String text = read(file);
			check(text.contains("items.equipment.weapon.missiles.thrower.desc="), file + "缺少投掷器描述");
			check(text.contains("items.equipment.weapon.missiles.thrower.ac_load="), file + "缺少装填动作名");
			check(text.contains("items.equipment.weapon.missiles.thrower.out_of_range="), file + "缺少射程提示");
		}
	}

	private static String read(String path) throws Exception {
		return new String(Files.readAllBytes(Path.of(path)), StandardCharsets.UTF_8);
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsThrowerTest() { }
}
