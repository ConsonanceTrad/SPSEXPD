package com.shatteredpixel.shatteredpixeldungeon.items.weapon.spammo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArmorBreak;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AttackUp;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DefenceUp;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.NormalCell;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.PocketBall;
import com.shatteredpixel.shatteredpixeldungeon.items.StoneOre;
import com.shatteredpixel.shatteredpixeldungeon.items.Torch;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.BuildBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.food.WaterItem;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.FruitCandy;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.Gel;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.MixPizza;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.NutCookie;
import com.shatteredpixel.shatteredpixeldungeon.items.food.fruit.Fruit;
import com.shatteredpixel.shatteredpixeldungeon.items.food.fusion.Nut;
import com.shatteredpixel.shatteredpixeldungeon.items.food.meatfood.MeatFood;
import com.shatteredpixel.shatteredpixeldungeon.items.food.staplefood.StapleFood;
import com.shatteredpixel.shatteredpixeldungeon.items.food.vegetable.Vegetable;
import com.shatteredpixel.shatteredpixeldungeon.items.medicine.Timepill2;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DarkGold;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicalInfusion;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.throwing.Boomerang;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.plants.BlandfruitBush;
import com.shatteredpixel.shatteredpixeldungeon.plants.Blindweed;
import com.shatteredpixel.shatteredpixeldungeon.plants.Dewcatcher;
import com.shatteredpixel.shatteredpixeldungeon.plants.Dreamfoil;
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot;
import com.shatteredpixel.shatteredpixeldungeon.plants.Fadeleaf;
import com.shatteredpixel.shatteredpixeldungeon.plants.Firebloom;
import com.shatteredpixel.shatteredpixeldungeon.plants.Freshberry;
import com.shatteredpixel.shatteredpixeldungeon.plants.Icecap;
import com.shatteredpixel.shatteredpixeldungeon.plants.NutPlant;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.plants.ReNepenth;
import com.shatteredpixel.shatteredpixeldungeon.plants.Rotberry;
import com.shatteredpixel.shatteredpixeldungeon.plants.Seedpod;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sorrowmoss;
import com.shatteredpixel.shatteredpixeldungeon.plants.StarEater;
import com.shatteredpixel.shatteredpixeldungeon.plants.Starflower;
import com.shatteredpixel.shatteredpixeldungeon.plants.Stormvine;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sungrass;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndIronMaker;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/** Headless checks for all legacy special ammunition and forge recipes. */
public final class SpsAmmoTest {

	private static final Class<?>[] AMMO_TYPES = {
			HeavyAmmo.class, FireAmmo.class, IceAmmo.class, StormAmmo.class, MossAmmo.class,
			BlindAmmo.class, StarAmmo.class, DreamAmmo.class, DewAmmo.class, SunAmmo.class,
			SandAmmo.class, GoldAmmo.class, EmptyAmmo.class, RotAmmo.class, EvolveAmmo.class,
			ThornAmmo.class, BattleAmmo.class, WoodenAmmo.class
	};
	private static final Integer[] GLOW_COLORS = {
			0x000000, 0xFF4400, 0x0000FF, 0xFFFFFF, 0x8844CC,
			0xFFFF44, 0x000000, 0x22CC44, null, 0xCCAA88,
			0xCCCCCC, 0xFFFF44, null, 0xCC0000, 0x006633,
			0xCC6600, 0x006633, 0x000000
	};

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Random.pushGenerator(0x535053414D4D4F4CL);
		try {
			testItemDefinitions();
			testBoomerangLoadingAndSave();
			testDeterministicEffectsAndBounds();
			testEvolutionSafety();
			testForgeRecipes();
			System.out.println("SPS特殊弹药与铁砧测试通过：18种弹药、全部旧版铁砧分支、回旋镖装填存档、战斗效果及异常边界均正常。");
		} finally {
			Random.popGenerator();
		}
	}

	private static void testItemDefinitions() throws Exception {
		for (int i = 0; i < AMMO_TYPES.length; i++) {
			SpAmmo ammo = (SpAmmo)AMMO_TYPES[i].getDeclaredConstructor().newInstance();
			check(!ammo.stackable && ammo.isIdentified() && !ammo.isUpgradable(),
					AMMO_TYPES[i].getSimpleName() + "的堆叠、鉴定或强化属性错误");
			check(ammo.value() == 100, AMMO_TYPES[i].getSimpleName() + "的价值不是100");
			Integer expected = GLOW_COLORS[i];
			check(expected == null ? ammo.glowing() == null
					: ammo.glowing() != null && ammo.glowing().color == expected,
					AMMO_TYPES[i].getSimpleName() + "的辉光颜色错误");
		}
	}

	private static void testBoomerangLoadingAndSave() {
		Hero hero = new Hero();
		Dungeon.hero = hero;
		Boomerang boomerang = new Boomerang();
		FireAmmo fire = new FireAmmo();
		hero.belongings.backpack.items.add(fire);
		check(boomerang.loadAmmoFromBackpack(hero, fire), "回旋镖无法从背包装填弹药");
		check(boomerang.loadedAmmo() == fire && !hero.belongings.backpack.contains(fire),
				"装填没有消耗一枚弹药或没有记录涂层");
		Bundle bundle = new Bundle();
		boomerang.storeInBundle(bundle);
		Boomerang restored = new Boomerang();
		restored.restoreFromBundle(bundle);
		check(restored.loadedAmmo() instanceof FireAmmo, "回旋镖弹药没有随存档恢复");
	}

	private static void testDeterministicEffectsAndBounds() {
		TestMob attacker = new TestMob(1000);
		TestMob defender = new TestMob(1000);
		new HeavyAmmo().onHit(attacker, defender, 40);
		check(defender.HP == 970, "重铅弹没有造成75%额外伤害");

		new BattleAmmo().onHit(attacker, defender, 40);
		check(defender.HP == 950 && attacker.buff(AttackUp.class) != null
				&& attacker.buff(DefenceUp.class) != null, "战斗弹的伤害或攻防强化错误");

		new DreamAmmo().onHit(attacker, defender, 40);
		check(defender.HP == 942 && defender.buff(ArmorBreak.class) != null
				&& defender.buff(ArmorBreak.class).level() == 25 && defender.buff(Slow.class) != null,
				"催眠弹的暗伤、破甲或减速错误");

		Boomerang boomerang = new Boomerang();
		boomerang.loadAmmo(new HeavyAmmo());
		TestMob boomerangTarget = new TestMob(1000);
		check(boomerang.proc(attacker, boomerangTarget, 20) == 20 && boomerangTarget.HP == 985,
				"回旋镖没有把命中伤害传给装填弹药");

		TestMob durable = new TestMob(10000);
		ThornAmmo thorn = new ThornAmmo();
		for (int i = 0; i < 200; i++) thorn.onHit(attacker, durable, 1);
		new DewAmmo().onHit(attacker, durable, 1);
		check(durable.isAlive(), "低伤害弹药边界导致目标异常死亡");

		Dungeon.gold = 1;
		new GoldAmmo().onHit(attacker, durable, 20);
		check(Dungeon.gold >= 0, "彩票弹把金币扣成了负数");
	}

	private static void testEvolutionSafety() {
		TestLevel level = new TestLevel();
		Dungeon.level = level;
		TestMob attacker = new TestMob(100);
		TestMob ordinary = new TestMob(73);
		ordinary.pos = 27;
		level.mobs.add(ordinary);
		check(EvolveAmmo.transform(attacker, ordinary), "退化弹不能转换普通怪物");
		check(!level.mobs.contains(ordinary), "退化弹转换后仍残留原怪物");
		Mob result = level.findMob(27);
		check(result instanceof NormalCell && result.HP == 73 && result.HT == 73,
				"退化弹没有生成等生命值的生命细胞");

		TestBoss boss = new TestBoss();
		boss.pos = 28;
		level.mobs.add(boss);
		check(!EvolveAmmo.transform(attacker, boss) && level.mobs.contains(boss),
				"退化弹错误转换了首领");
	}

	private static void testForgeRecipes() throws Exception {
		assertRecipe(HeavyAmmo.class, new StoneOre(), new StoneOre());
		assertRecipe(WoodenAmmo.class, new StoneOre(), new NutPlant.Seed());
		assertRecipe(FireAmmo.class, new StoneOre(), new Firebloom.Seed());
		assertRecipe(IceAmmo.class, new StoneOre(), new Icecap.Seed());
		assertRecipe(StormAmmo.class, new StoneOre(), new Stormvine.Seed());
		assertRecipe(MossAmmo.class, new StoneOre(), new Sorrowmoss.Seed());
		assertRecipe(BlindAmmo.class, new StoneOre(), new Blindweed.Seed());
		assertRecipe(StarAmmo.class, new StoneOre(), new Starflower.Seed());
		assertRecipe(DreamAmmo.class, new StoneOre(), new Dreamfoil.Seed());
		assertRecipe(DewAmmo.class, new StoneOre(), new Dewcatcher.Seed());
		assertRecipe(SunAmmo.class, new StoneOre(), new Sungrass.Seed());
		assertRecipe(SandAmmo.class, new StoneOre(), new Fadeleaf.Seed());
		assertRecipe(GoldAmmo.class, new StoneOre(), new Seedpod.Seed());
		assertRecipe(RotAmmo.class, new StoneOre(), new Rotberry.Seed());
		assertRecipe(RotAmmo.class, new StoneOre(), new Freshberry.Seed());
		assertRecipe(ThornAmmo.class, new StoneOre(), new Earthroot.Seed());
		assertRecipe(EmptyAmmo.class, new StoneOre(), new BlandfruitBush.Seed());
		assertRecipe(EvolveAmmo.class, new StoneOre(), new ReNepenth.Seed());
		assertRecipe(BattleAmmo.class, new StoneOre(), new StarEater.Seed());

		assertRecipe(NutCookie.class, 6, new Nut(), new Nut(), new Nut(), new Nut(), new Nut());
		assertRecipe(Timepill2.class, new StoneOre(), new StoneOre(), new StoneOre(), new StoneOre(), new WaterItem());
		assertRecipe(MixPizza.class, 8, new MeatFood(), new StoneOre(), new StapleFood(), new Fruit(), new Vegetable());
		assertRecipe(FruitCandy.class, 2, new WaterItem(), new Fruit(), new StoneOre());
		assertRecipe(Bomb.class, new BuildBomb(), new Firebloom.Seed(), new Icecap.Seed());
		assertRecipe(PocketBall.class, new DarkGold(), new DarkGold(), new DarkGold(), new StoneOre());
		assertRecipe(com.shatteredpixel.shatteredpixeldungeon.items.GreatRune.class, new ScrollOfMagicalInfusion());
		assertRecipe(Torch.class, new Gel());
	}

	private static void assertRecipe(Class<? extends Item> expected, Item... ingredients) throws Exception {
		assertRecipe(expected, -1, ingredients);
	}

	private static void assertRecipe(Class<? extends Item> expected, int quantity, Item... ingredients) throws Exception {
		Method recipe = WndIronMaker.class.getDeclaredMethod("recipe", ArrayList.class);
		recipe.setAccessible(true);
		ArrayList<Item> input = new ArrayList<>();
		for (Item item : ingredients) input.add(item);
		Item result = (Item)recipe.invoke(null, input);
		check(expected.isInstance(result), "铁匠配方错误：预期" + expected.getSimpleName()
				+ "，实际" + (result == null ? "null" : result.getClass().getSimpleName()));
		if (quantity >= 0) check(result.quantity() == quantity,
				"铁匠配方数量错误：" + expected.getSimpleName() + "应为" + quantity + "，实际" + result.quantity());
	}

	private static class TestMob extends Mob {
		TestMob(int health) { HP = HT = health; }
		@Override public int damageRoll() { return 1; }
		@Override public int attackSkill(Char target) { return 1; }
		@Override public int drRoll() { return 0; }
	}

	private static final class TestBoss extends TestMob {
		TestBoss() { super(100); properties.add(Property.BOSS); }
	}

	private static final class TestLevel extends Level {
		TestLevel() {
			setSize(8, 8);
			mobs = new HashSet<>();
			heaps = new com.watabou.utils.SparseArray<>();
			blobs = new HashMap<>();
			plants = new com.watabou.utils.SparseArray<Plant>();
			traps = new com.watabou.utils.SparseArray<>();
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

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsAmmoTest() { }
}
