package pd.items.equipment.weapon.guns;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.items.Magazine;
import pd.items.Recipe;
import pd.items.SpsAlchemyRecipes;
import pd.items.StoneOre;
import pd.items.quest.DarkGold;
import pd.atlas.items.ConsumUsefulUsefulDict;
import render.noosa.Game;

/**
 * SPSEXPD: 通用弹匣的无头校验——物品定义、一个弹匣装满任意容量枪械、
 * 无弹匣时失败、电磁手枪同样消耗弹匣、以及炼金配方（原石 + 暗金矿）。
 */
public final class SpsMagazineTest {

	public static void main(String[] args) {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		testDefinition();
		testFillsAnyCapacity();
		testFailsWithoutMagazine();
		testChargeGunConsumesMagazine();
		testAlchemyRecipe();
		System.out.println("通用弹匣测试通过：定义、一个弹匣装满任意容量、无弹匣失败、电磁手枪消耗与炼金配方均正常。");
	}

	private static void testDefinition() {
		Magazine m = new Magazine();
		check(m.image == ConsumUsefulUsefulDict.SP_AMMO, "通用弹匣图标错误");
		check(m.stackable, "通用弹匣不能堆叠");
		check(!m.isUpgradable(), "通用弹匣不应可升级");
		check(m.isIdentified(), "通用弹匣应默认识别");
		check(m.value() == 100, "通用弹匣单价错误");
		check(new Magazine().quantity(5).value() == 500, "通用弹匣堆叠价值错误");
	}

	/** 一个弹匣装满任意容量（与射击数无关），并消耗一个。 */
	private static void testFillsAnyCapacity() {
		checkFills(new GunA(), 7);
		checkFills(new GunB(), 20);
		checkFills(new GunC(), 4);
		checkFills(new GunD(), 5);
		checkFills(new GunE(), 6);
		checkFills(new Sling(), 1);
		checkFills(new ToyGun(), 10);
	}

	private static void checkFills(GunWeapon gun, int capacity) {
		Hero hero = new Hero();
		Dungeon.hero = hero;
		hero.belongings.backpack.items.add(new Magazine());
		int before = gun.charge();
		check(gun.reloadMagazine(hero), gun.getClass().getSimpleName() + " 无法用通用弹匣装填");
		check(gun.charge() == capacity, gun.getClass().getSimpleName() + " 装填后没有填满弹匣");
		check(before == 0, gun.getClass().getSimpleName() + " 初始弹匣不为空");
		check(!hero.belongings.backpack.contains(new Magazine()),
				gun.getClass().getSimpleName() + " 装填后没有消耗通用弹匣");
	}

	/** 没有通用弹匣时不得装填。 */
	private static void testFailsWithoutMagazine() {
		Hero hero = new Hero();
		Dungeon.hero = hero;
		GunA gun = new GunA();
		check(!gun.reloadMagazine(hero), "无通用弹匣时仍然装填成功");
		check(gun.charge() == 0, "无通用弹匣时装填改变了弹匣状态");
	}

	/** 电磁手枪（充能型枪械）同样可以用通用弹匣装填。 */
	private static void testChargeGunConsumesMagazine() {
		Hero hero = new Hero();
		Dungeon.hero = hero;
		hero.belongings.backpack.items.add(new Magazine());
		pd.items.misc.GunOfSoldier gun = new pd.items.misc.GunOfSoldier();
		check(gun.reloadMagazine(hero), "电磁手枪无法用通用弹匣装填");
		check(gun.charge() == pd.items.misc.GunOfSoldier.MAGAZINE, "电磁手枪没有被一个弹匣填满 3 发");
		check(!hero.belongings.backpack.contains(new Magazine()), "电磁手枪装填后没有消耗通用弹匣");
	}

	/** 通用弹匣由炼金配方「原石 + 暗金矿」产出。 */
	private static void testAlchemyRecipe() {
		boolean found = false;
		for (Recipe r : SpsAlchemyRecipes.allRecipes()) {
			if (!(r instanceof SpsAlchemyRecipes.TypedRecipe)) continue;
			SpsAlchemyRecipes.TypedRecipe typed = (SpsAlchemyRecipes.TypedRecipe) r;
			if (typed.output() != Magazine.class) continue;
			int stone = 0, dark = 0;
			for (Class<?> input : typed.inputs()) {
				if (input == StoneOre.class) stone++;
				if (input == DarkGold.class) dark++;
			}
			if (stone == 1 && dark == 1) found = true;
		}
		check(found, "缺少「原石 + 暗金矿 → 通用弹匣」的炼金配方");
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsMagazineTest() { }
}
