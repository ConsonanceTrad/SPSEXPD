package pd.items.equipment.weapon.guns;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.items.Waterskin;
import pd.items.equipment.bombs.Firebomb;
import render.noosa.Game;

/**
 * SPSEXPD: 特殊枪械（弹药耦合）的无头校验——露珠炮以露珠为弹药、爆弹发射器以炸弹为弹药。
 */
public final class SpsSpecialGunTest {

	public static void main(String[] args) {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		testDewCannon();
		testBombLauncher();
		System.out.println("特殊枪械测试通过：露珠炮（15 露珠 → 3 发）与爆弹发射器（1 炸弹 → 1 发）装填规则正常。");
	}

	/** 露珠炮：一次装填扣露珠瓶 15 点露珠，填充 3 发。 */
	private static void testDewCannon() {
		Hero hero = new Hero();
		Dungeon.hero = hero;
		DewCannon gun = new DewCannon();
		check(gun.fullCharge() == 3 && DewCannon.DEW_COST == 15, "露珠炮弹匣容量或露珠消耗错误");
		check(!gun.reloadMagazine(hero), "没有露珠瓶时露珠炮仍能装填");

		Waterskin flask = new Waterskin();
		flask.fill();
		hero.belongings.backpack.items.add(flask);
		int before = flask.totalDew();
		check(gun.reloadMagazine(hero) && gun.charge() == 3, "露珠炮没有被一次装填填满 3 发");
		check(before - flask.totalDew() == 15, "露珠炮装填没有扣除 15 点露珠");
		check(!gun.reloadMagazine(hero), "满弹匣的露珠炮仍能继续装填");
	}

	/** 爆弹发射器：一次装填消耗背包一个炸弹，弹匣容量 1，并记住炸弹类型。 */
	private static void testBombLauncher() {
		Hero hero = new Hero();
		Dungeon.hero = hero;
		BombLauncher gun = new BombLauncher();
		check(gun.fullCharge() == 1, "爆弹发射器弹匣容量不是 1");
		check(!gun.reloadMagazine(hero), "没有炸弹时爆弹发射器仍能装填");

		Firebomb bomb = new Firebomb();
		hero.belongings.backpack.items.add(bomb);
		check(gun.reloadMagazine(hero) && gun.charge() == 1, "爆弹发射器没有被一个炸弹装填");
		check(gun.loadedBomb() == bomb, "爆弹发射器没有记住装填的炸弹");
		check(!hero.belongings.backpack.contains(bomb), "装填后炸弹没有从背包容器中消耗");
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsSpecialGunTest() { }
}
