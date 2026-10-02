package pd.items.equipment.rings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.QuickSlot;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.items.Heap;
import pd.items.Item;
import pd.items.equipment.armor.normalarmor.ClothArmor;
import pd.items.equipment.weapon.melee.normalweapon.ShortSword;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.traps.Trap;
import pd.plants.Plant;
import render.noosa.Game;
import render.utils.data.SparseArray;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public final class SpsLegacyRingsTest {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Ring.initGems();
		try {
			testAccuracyAndReach();
			testEvasionArmorStats();
			testFurorSpeedAndDamage();
			testHasteSpeed();
			testRawRingLevels();
			testResources();
			System.out.println("SPS普通戒指测试通过：精准、距离、护甲属性、攻速、伤害、移速、负等级及双语原文均正常。");
		} finally {
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
		}
	}

	private static void testAccuracyAndReach() {
		Hero hero = heroOnLevel();
		RingOfAccuracy ring = active(new RingOfAccuracy(), hero, 10);
		check(close(RingOfAccuracy.accuracyMultiplier(hero), (float)Math.pow(0.75, -10)),
				"+10精准戒指命中倍率错误");
		check(RingOfAccuracy.reachBonus(hero) == 1, "+10精准戒指没有增加1格距离");

		FixedSword sword = new FixedSword();
		hero.belongings.weapon = sword;
		check(sword.reachFactor(hero) == 2, "+10精准戒指没有增加武器实际距离");
		TestMob near = mobAt(42);
		TestMob far = mobAt(44);
		check(hero.canAttack(near), "+10精准戒指不能进行2格武器攻击");
		check(!hero.canAttack(far), "+10精准戒指错误允许4格武器攻击");

		ring.level(30);
		check(close(RingOfAccuracy.accuracyMultiplier(hero), (float)Math.pow(0.75, -30)),
				"+30精准戒指命中倍率错误");
		check(RingOfAccuracy.reachBonus(hero) == 3 && sword.reachFactor(hero) == 4,
				"+30精准戒指没有达到3格距离加成");
		check(hero.canAttack(far), "+30精准戒指不能进行4格武器攻击");

		hero.belongings.weapon = null;
		check(hero.canAttack(far), "+30精准戒指不能进行4格空手攻击");

		ring.level(-2);
		check(close(RingOfAccuracy.accuracyMultiplier(hero), 0.5625f), "负等级精准戒指没有降低命中");
		check(RingOfAccuracy.reachBonus(hero) == 0, "负等级精准戒指错误增加攻击距离");
	}

	private static void testEvasionArmorStats() {
		Hero hero = heroOnLevel();
		TestMob enemy = mobAt(42);
		int unarmored = hero.defenseSkill(enemy);
		RingOfEvasion ring = active(new RingOfEvasion(), hero, 15);
		check(hero.defenseSkill(enemy) == unarmored, "闪避戒指在未穿护甲时错误提高防御");

		ClothArmor armor = new ClothArmor();
		hero.belongings.armor = armor;
		check(RingOfEvasion.dexterityBonus(hero) == 1, "+15闪避戒指DEX加成错误");
		check(RingOfEvasion.stealthBonus(hero) == 3, "+15闪避戒指潜行加成错误");
		check(close(armor.evasionFactor(hero, 1f), 3f), "+15闪避戒指没有接入护甲DEX");
		check(close(armor.stealthFactor(hero), 9f), "+15闪避戒指没有接入护甲潜行");

		ring.level(30);
		check(RingOfEvasion.dexterityBonus(hero) == 2 && RingOfEvasion.stealthBonus(hero) == 6,
				"+30闪避戒指没有达到DEX或潜行上限");
		check(close(armor.evasionFactor(hero, 1f), 4f) && close(armor.stealthFactor(hero), 12f),
				"+30闪避戒指护甲实际属性错误");

		ring.level(-2);
		check(RingOfEvasion.dexterityBonus(hero) == 0 && RingOfEvasion.stealthBonus(hero) == 0,
				"-2闪避戒指整数分段行为与旧版不符");
	}

	private static void testFurorSpeedAndDamage() {
		Hero hero = heroOnLevel();
		FixedSword sword = new FixedSword();
		hero.belongings.weapon = sword;
		RingOfFuror ring = active(new RingOfFuror(), hero, 10);
		check(close(RingOfFuror.attackSpeedMultiplier(hero), 2f), "+10狂怒戒指攻速错误");
		check(close(sword.delayFactor(hero), 0.5f), "+10狂怒戒指没有接入武器攻击耗时");
		check(RingOfFuror.damageBonus(hero) == 10 && hero.damageRoll() == 17,
				"+10狂怒戒指没有增加实际基础伤害");

		ring.level(30);
		check(close(RingOfFuror.attackSpeedMultiplier(hero), 4f), "+30狂怒戒指没有达到4倍上限");
		check(close(sword.delayFactor(hero), 0.25f), "+30狂怒戒指武器耗时错误");
		check(RingOfFuror.damageBonus(hero) == 30 && hero.damageRoll() == 37,
				"+30狂怒戒指伤害加成错误");

		ring.level(-2);
		check(close(RingOfFuror.attackSpeedMultiplier(hero), 0.8f), "负等级狂怒戒指没有降低攻速");
		check(close(sword.delayFactor(hero), 1.25f), "负等级狂怒戒指没有增加攻击耗时");
		check(RingOfFuror.damageBonus(hero) == 0 && hero.damageRoll() == 7,
				"负等级狂怒戒指错误倒扣基础伤害");
	}

	private static void testHasteSpeed() {
		Hero hero = heroOnLevel();
		RingOfHaste ring = active(new RingOfHaste(), hero, 10);
		check(close(RingOfHaste.speedMultiplier(hero), 2f) && close(hero.speed(), 2f),
				"+10疾速戒指移动速度错误");
		ring.level(30);
		check(close(RingOfHaste.speedMultiplier(hero), 4f) && close(hero.speed(), 4f),
				"+30疾速戒指没有达到4倍上限");
		ring.level(-2);
		check(close(RingOfHaste.speedMultiplier(hero), 0.8f) && close(hero.speed(), 0.8f),
				"负等级疾速戒指没有降低移动速度");
	}

	private static void testRawRingLevels() {
		Hero hero = heroOnLevel();
		active(new RingOfAccuracy(), hero, 0);
		active(new RingOfEvasion(), hero, 0);
		active(new RingOfFuror(), hero, 0);
		active(new RingOfHaste(), hero, 0);
		check(close(RingOfAccuracy.accuracyMultiplier(hero), 1f), "精准戒指错误使用level+1");
		check(RingOfEvasion.dexterityBonus(hero) == 0 && RingOfEvasion.stealthBonus(hero) == 0,
				"闪避戒指错误使用level+1");
		check(close(RingOfFuror.attackSpeedMultiplier(hero), 1f) && RingOfFuror.damageBonus(hero) == 0,
				"狂怒戒指错误使用level+1");
		check(close(RingOfHaste.speedMultiplier(hero), 1f), "疾速戒指错误使用level+1");
	}

	private static void testResources() throws Exception {
		String zh = Files.readString(Paths.get("messages/items/zh/items.properties"), StandardCharsets.UTF_8);
		String en = Files.readString(Paths.get("messages/items/en/items.properties"), StandardCharsets.UTF_8);
		check(zh.contains("items.rings.ringofaccuracy.name=精准戒指")
				&& zh.contains("攻击距离会增加_%2$d_格")
				&& zh.contains("闪避值会增加_%d_点，潜行会增加_%2$d_点")
				&& zh.contains("基础伤害提升_%2$s_点")
				&& zh.contains("items.rings.ringofhaste.name=疾速戒指")
				&& !zh.contains("�"), "四枚普通戒指中文原文缺失或乱码");
		check(en.contains("ACU improve _ %1$d _ , Attack range improve _%2$d_ .")
				&& en.contains("Dex improve _%d_ , steath improve _%2$d_ .")
				&& en.contains("Melee attack speed improve _%s%%_ .")
				&& en.contains("Move speed improve _%s%%_ ."), "四枚普通戒指英文原文缺失");
	}

	private static <T extends Ring> T active(T ring, Hero hero, int level) {
		ring.level(level);
		ring.activate(hero);
		return ring;
	}

	private static Hero heroOnLevel() {
		Actor.clear();
		Dungeon.quickslot = new QuickSlot();
		Dungeon.level = new TestLevel();
		Hero hero = new Hero();
		hero.pos = 40;
		hero.STR = 20;
		Dungeon.hero = hero;
		Actor.add(hero);
		return hero;
	}

	private static TestMob mobAt(int pos) {
		TestMob mob = new TestMob();
		mob.pos = pos;
		Dungeon.level.mobs().add(mob);
		Actor.add(mob);
		return mob;
	}

	private static boolean close(float first, float second) {
		return Math.abs(first - second) < 0.0001f;
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private static final class FixedSword extends ShortSword {
		@Override public int damageRoll(Char owner) { return 7; }
	}

	private static final class TestMob extends Mob {
		@Override public int damageRoll() { return 1; }
		@Override public int attackSkill(Char target) { return 1; }
	}

	private static final class TestLevel extends Level {
		TestLevel() {
			setSize(9, 9);
			Arrays.fill(map, Terrain.EMPTY);
			mobs().clear();
			heaps = new SparseArray<>();
			blobs = new HashMap<>();
			plants = new SparseArray<Plant>();
			traps = new SparseArray<Trap>();
			transitions = new ArrayList<>();
			customTiles = new ArrayList<>();
			customTerrain = new ArrayList<>();
			customWalls = new ArrayList<>();
			buildFlagMaps();
			discoverable = new boolean[length()];
			Arrays.fill(discoverable, true);
			Arrays.fill(heroFOV, true);
		}

		@Override protected boolean build() { return true; }
		@Override protected void createMobs() { }
		@Override protected void createItems() { }
		@Override public String tilesTex() { return null; }
		@Override public String waterTex() { return null; }
		@Override public Heap drop(Item item, int cell) { return null; }
	}

	private SpsLegacyRingsTest() { }
}
