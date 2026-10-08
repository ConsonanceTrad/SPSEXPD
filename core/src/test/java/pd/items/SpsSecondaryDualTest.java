package pd.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.items.equipment.armor.Armor;
import pd.items.equipment.armor.normalarmor.BaseArmor;
import pd.items.equipment.armor.normalarmor.VestArmor;
import pd.items.equipment.weapon.Weapon;
import pd.items.equipment.weapon.melee.normalweapon.ShortSword;
import pd.items.equipment.weapon.melee.normalweapon.Spear;
import pd.items.misc.MissileShield;
import pd.levels.Level;
import pd.plants.Plant;
import pd.sprites.ItemSprite;
import render.noosa.Game;
import render.utils.data.SparseArray;
import render.utils.math.Random;

import java.util.ArrayList;
import java.util.HashMap;

/** SPSEXPD 无头校验：副手装备体系（力量需求 +50%、0.85 倍命中、副武器连携、副甲防护与刻印、副甲减速）。 */
public final class SpsSecondaryDualTest {

	public static void main(String[] args) {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Random.pushGenerator(0x535053534455414CL);
		try {
			Dungeon.level = new TestLevel();
			testWeaponStrengthReq();
			testArmorStrengthReq();
			testSecondaryStrike();
			testSecondaryArmor();
			testSecondaryArmorSpeed();
			testEquipActionsAndOverweight();
			System.out.println("SPSEXPD副手系统测试通过：力量需求+50%、0.85倍命中修正、副武器连携攻击（不耗回合）、副护甲防护与刻印、副护甲减速均正常。");
		} finally {
			Dungeon.hero = null;
			Dungeon.level = null;
			Random.popGenerator();
		}
	}

	/** 双持时两把武器力量需求都提高 50%（向上取整），且不波及其它物品。 */
	private static void testWeaponStrengthReq() {
		Hero hero = newHero();
		ShortSword primary = new ShortSword();
		Spear second = new Spear();

		hero.belongings.weapon = primary;
		check(primary.STRReq() == primary.STRReq(0), "单持主武器不应有力量需求惩罚");

		hero.belongings.secondWep = second;
		check(SecondaryEquip.dualWeapons(hero), "装备了主副两把武器却没有判定为双持");
		check(primary.STRReq() == (int)Math.ceil(primary.STRReq(0) * 1.5f), "双持时主武器力量需求没有提高50%");
		check(second.STRReq() == (int)Math.ceil(second.STRReq(0) * 1.5f), "双持时副武器力量需求没有提高50%");
		check(SecondaryEquip.increasedStrengthReq(primary.STRReq(0)) == (int)Math.ceil(primary.STRReq(0) * 1.5f),
				"力量需求+50%没有向上取整");

		Spear spare = new Spear();
		check(spare.STRReq() == spare.STRReq(0), "背包中的武器不应受双持力量需求惩罚");

		//SPSEXPD: 神木圆盾等只能进副手栏的装备不参与双持规则
		hero.belongings.secondWep = new MissileShield();
		check(!SecondaryEquip.dualWeapons(hero), "神木圆盾不应算作双持");
		check(primary.STRReq() == primary.STRReq(0), "副手为盾时主武器不应有力量需求惩罚");
		check(SecondaryEquip.hitMultiplier(hero) == 1f, "副手为盾时不应有命中修正");
	}

	/** 双甲时两件护甲力量需求都提高 50%，单件或主手为空时不惩罚。 */
	private static void testArmorStrengthReq() {
		Hero hero = newHero();
		BaseArmor primary = new BaseArmor();
		VestArmor second = new VestArmor();

		hero.belongings.armor = primary;
		check(primary.STRReq() == primary.STRReq(0), "单件护甲不应有力量需求惩罚");

		hero.belongings.secondArmor = second;
		check(SecondaryEquip.dualArmor(hero), "装备了主副两件护甲却没有判定为双甲");
		check(primary.STRReq() == (int)Math.ceil(primary.STRReq(0) * 1.5f), "双甲时主护甲力量需求没有提高50%");
		check(second.STRReq() == (int)Math.ceil(second.STRReq(0) * 1.5f), "双甲时副护甲力量需求没有提高50%");

		hero.belongings.secondArmor = null;
		check(primary.STRReq() == primary.STRReq(0), "卸下副甲后主护甲仍残留力量需求惩罚");
	}

	/** 副武器紧随主武器进行连携攻击：使用副武器数值、不消耗回合，且副手为盾/空时不触发。 */
	private static void testSecondaryStrike() {
		Hero hero = newHero();
		hero.belongings.weapon = new TestWeapon(1);
		hero.belongings.secondWep = new TestWeapon(100);
		TestMob target = new TestMob(1_000_000);

		check(SecondaryEquip.hitMultiplier(hero) == 0.85f, "双持时主武器命中修正不是0.85倍");
		check(Hero.SECONDARY_STRIKE_GAP == 0.05f, "连携攻击的声效间隔不是0.05秒");

		float cooldown = hero.cooldown();
		int before = target.HP;
		boolean struck = false;
		for (int i = 0; i < 30 && !struck; i++) struck = hero.secondaryStrike(target);
		check(struck, "双持时没有发动副武器连携攻击");
		check(target.HP <= before - 100, "连携攻击没有使用副武器的伤害数值");
		check(hero.cooldown() == cooldown, "副武器连携攻击不应消耗回合");
		check(hero.belongings.abilityWeapon == null, "连携攻击后没有还原 abilityWeapon");

		hero.belongings.secondWep = null;
		check(SecondaryEquip.hitMultiplier(hero) == 1f, "未装备副武器时不应有命中修正");
		check(!hero.secondaryStrike(target), "未装备副武器时不应发动连携攻击");

		hero.belongings.secondWep = new MissileShield();
		check(!hero.secondaryStrike(target), "副手为盾时不应发动连携攻击");
	}

	/** 副护甲与主护甲一样提供护甲值并触发刻印。 */
	private static void testSecondaryArmor() {
		Hero hero = newHero();
		hero.belongings.armor = new TestArmor(1, 0, 0);
		TestArmor second = new TestArmor(1, 10, 10);
		hero.belongings.secondArmor = second;

		int lowest = Integer.MAX_VALUE;
		for (int i = 0; i < 200; i++) lowest = Math.min(lowest, hero.drRoll());
		check(lowest >= 10, "副护甲没有提供护甲值");

		TestGlyph.PROCS = 0;
		second.inscribe(new TestGlyph());
		hero.defenseProc(new TestMob(100), 100);
		check(TestGlyph.PROCS > 0, "副护甲的刻印没有生效");

		second.inscribe(null);
		int before = TestGlyph.PROCS;
		hero.defenseProc(new TestMob(100), 100);
		check(TestGlyph.PROCS == before, "副护甲取下刻印后仍在触发");
	}

	/** 副护甲每 1 阶位使攻击与移动速度降低 20%（累乘 0.8^tier）。 */
	private static void testSecondaryArmorSpeed() {
		Hero hero = newHero();
		hero.belongings.weapon = new TestWeapon(1);

		float baseSpeed = hero.speed();
		float baseDelay = hero.attackDelay();

		for (int tier = 1; tier <= 5; tier++) {
			hero.belongings.secondArmor = new TestArmor(tier, 0, 0);
			float expected = (float)Math.pow(0.8, tier);
			check(close(SecondaryEquip.armorSpeedMultiplier(hero), expected),
					"副护甲减速倍率不是0.8^tier（tier=" + tier + "）");
			check(close(hero.speed(), baseSpeed * expected), "副护甲没有按阶位降低移动速度（tier=" + tier + "）");
			check(close(hero.attackDelay(), baseDelay / expected), "副护甲没有按阶位降低攻击速度（tier=" + tier + "）");
			check(close(hero.attackDelay() * hero.speed(), baseDelay * baseSpeed),
					"副护甲的攻速与移速惩罚比例不一致（tier=" + tier + "）");
		}

		hero.belongings.secondArmor = null;
		check(close(hero.speed(), baseSpeed) && close(hero.attackDelay(), baseDelay), "卸下副甲后速度惩罚仍然存在");
	}

	/** 动作栏同时提供「装备」与「副手装备」；超力量提示只在副手装备（双持/双甲）时判定。 */
	private static void testEquipActionsAndOverweight() {
		Hero hero = newHero();
		hero.STR = 10;

		ShortSword primary = new ShortSword();
		ArrayList<String> actions = primary.actions(hero);
		check(actions.contains(EquipableItem.AC_EQUIP) && actions.contains(EquipableItem.AC_EQUIP_SECONDARY),
				"武器动作栏没有同时提供装备与副手装备");
		check(!actions.contains(EquipableItem.AC_UNEQUIP), "未装备的武器不应提供取下动作");
		//SPSEXPD: 「装备」固定另起一行（动作窗第二行），「副手装备」紧跟其后 ⇒ 二者并排同占第二行
		check(primary.actionBreakBefore(EquipableItem.AC_EQUIP)
				&& !primary.actionBreakBefore(Item.AC_DROP)
				&& !primary.actionBreakBefore(EquipableItem.AC_EQUIP_SECONDARY),
				"武器装备动作没有固定另起一行");
		check(actions.indexOf(EquipableItem.AC_EQUIP) + 1 == actions.indexOf(EquipableItem.AC_EQUIP_SECONDARY),
				"副手装备没有紧跟在装备动作之后");

		hero.belongings.weapon = primary;
		ArrayList<String> equipped = primary.actions(hero);
		check(equipped.contains(EquipableItem.AC_UNEQUIP)
				&& !equipped.contains(EquipableItem.AC_EQUIP_SECONDARY),
				"已装备的武器仍然提供副手装备动作或缺少取下动作");

		check(SecondaryEquip.dualWieldTooHeavy(hero, primary, new Spear()), "双持超出力量时没有超力量提示");
		check(!SecondaryEquip.dualWieldTooHeavy(hero, primary, null), "只有主武器时不应提示");
		check(!SecondaryEquip.dualWieldTooHeavy(hero, primary, new MissileShield()), "副手为盾时不应提示");
		hero.STR = 60;
		check(!SecondaryEquip.dualWieldTooHeavy(hero, primary, new Spear()), "力量充足时不应有超力量提示");

		//SPSEXPD: 护甲同样是「装备」+「副手装备」，且双甲也会超出力量
		hero.STR = 10;
		BaseArmor armor = new BaseArmor();
		ArrayList<String> armorActions = armor.actions(hero);
		check(armorActions.contains(EquipableItem.AC_EQUIP) && armorActions.contains(EquipableItem.AC_EQUIP_SECONDARY),
				"护甲动作栏没有同时提供装备与副手装备");
		check(armor.actionBreakBefore(EquipableItem.AC_EQUIP)
				&& armorActions.indexOf(EquipableItem.AC_EQUIP) + 1 == armorActions.indexOf(EquipableItem.AC_EQUIP_SECONDARY),
				"护甲装备动作没有固定另起一行或副手装备未紧跟其后");
		check(SecondaryEquip.dualArmorTooHeavy(hero, armor, new VestArmor()), "双甲超出力量时没有超力量提示");
		check(!SecondaryEquip.dualArmorTooHeavy(hero, armor, null), "只有主护甲时不应提示");
		hero.STR = 60;
		check(!SecondaryEquip.dualArmorTooHeavy(hero, armor, new VestArmor()), "力量充足时双甲不应有超力量提示");

		//SPSEXPD: 只能进副手栏的武器（神木圆盾）只提供副手装备
		ArrayList<String> shieldActions = new MissileShield().actions(hero);
		check(shieldActions.contains(EquipableItem.AC_EQUIP_SECONDARY)
				&& !shieldActions.contains(EquipableItem.AC_EQUIP),
				"只能进副手栏的武器没有隐藏主手装备动作");
	}

	private static Hero newHero() {
		Hero hero = new Hero();
		hero.HP = hero.HT = 100;
		hero.pos = 1;
		hero.STR = 100;
		Dungeon.hero = hero;
		return hero;
	}

	private static boolean close(float a, float b) {
		return Math.abs(a - b) < 0.0001f;
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	/** 固定伤害的测试武器：命中值极高以保证命中。 */
	private static final class TestWeapon extends Weapon {
		private final int dmg;
		TestWeapon(int dmg) { this.dmg = dmg; }
		@Override public int min(int lvl) { return dmg; }
		@Override public int max(int lvl) { return dmg; }
		@Override public int STRReq(int lvl) { return 0; }
		@Override public float accuracyFactor(Char owner, Char target) { return 1000f; }
	}

	private static final class TestArmor extends Armor {
		private final int min, max;
		TestArmor(int tier, int min, int max) {
			super(tier);
			this.min = min;
			this.max = max;
			this.levelKnown = true;
		}
		@Override public int DRMin(int lvl) { return min; }
		@Override public int DRMax(int lvl) { return max; }
		@Override public int STRReq(int lvl) { return 0; }
	}

	private static final class TestGlyph extends Armor.Glyph {
		private static int PROCS = 0;
		@Override public int proc(Armor armor, Char attacker, Char defender, int damage) {
			PROCS++;
			return damage - 1;
		}
		@Override public ItemSprite.Glowing glowing() { return null; }
	}

	private static final class TestMob extends Mob {
		TestMob(int health) { HP = HT = health; }
		@Override public int damageRoll() { return 20; }
		@Override public int attackSkill(Char target) { return 1_000_000; }
		@Override public int defenseSkill(Char enemy) { return 0; }
		@Override public int drRoll() { return 0; }
	}

	private static final class TestLevel extends Level {
		TestLevel() {
			setSize(8, 8);
			mobs().clear();
			heaps = new SparseArray<>();
			blobs = new HashMap<>();
			plants = new SparseArray<Plant>();
			traps = new SparseArray<>();
			transitions = new ArrayList<>();
			customTiles = new ArrayList<>();
			customTerrain = new ArrayList<>();
			customWalls = new ArrayList<>();
		}
		@Override protected boolean build() { return true; }
		@Override protected void createMobs() { }
		@Override protected void createItems() { }
		@Override public String tilesTex() { return null; }
		@Override public String waterTex() { return null; }
		@Override public Heap drop(Item item, int cell) { return null; }
	}

	private SpsSecondaryDualTest() { }
}
