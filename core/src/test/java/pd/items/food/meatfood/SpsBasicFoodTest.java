package pd.items.food.meatfood;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.buffs.Barkskin;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import pd.actors.buffs.Drowsy;
import pd.actors.buffs.FunnyBuff;
import pd.actors.buffs.Invisibility;
import pd.actors.buffs.Poison;
import pd.actors.buffs.STRDown;
import pd.actors.buffs.Slow;
import pd.actors.buffs.Vertigo;
import pd.actors.hero.Hero;
import pd.actors.mobs.Rat;
import pd.items.Generator;
import pd.items.Heap;
import pd.items.food.staplefood.NormalRation;
import pd.items.food.staplefood.OverpricedRation;
import pd.items.food.staplefood.Pasty;
import pd.sprites.ItemSpriteSheet;
import render.noosa.Game;
import render.utils.math.Random;

import java.util.Arrays;

public final class SpsBasicFoodTest {
	public static void main(String[] args) {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Random.pushGenerator(0x5350534241534943L);
		try {
			testStapleDeck();
			testDefinitions();
			testEffects();
			testHeapReactions();
			testMonsterDrop();
			System.out.println("SPS基础食物测试通过：旧版主食牌组、7种肉类、状态效果、怪物掉落及6类地面加工均正常。");
		} finally {
			Random.popGenerator();
		}
	}

	private static void testStapleDeck() {
		check(Arrays.equals(Generator.Category.FOOD.classes,
				new Class<?>[]{NormalRation.class, Pasty.class, OverpricedRation.class}), "普通食物牌组类型或顺序错误");
		check(Arrays.equals(Generator.Category.FOOD.defaultProbs, new float[]{8, 2, 5}), "普通食物牌组权重错误");
		NormalRation ration = new NormalRation();
		OverpricedRation small = new OverpricedRation();
		check(ration.energy == 300f && ration.image == ItemSpriteSheet.RATION && ration.value() == 5, "干粮包定义错误");
		check(small.energy == 200f && small.image == ItemSpriteSheet.OVERPRICED && small.value() == 3, "干粮小包定义错误");
	}

	private static void testDefinitions() {
		MeatFood[] meats = {new Meat(), new FireMeat(), new IceMeat(), new EarthMeat(),
				new ShockMeat(), new LightMeat(), new DarkMeat(), new FunnyFood()};
		float[] energy = {100, 150, 100, 100, 100, 100, 100, 500};
		int[] values = {2, 2, 3, 3, 3, 3, 3, 1};
		for (int i = 0; i < meats.length; i++) {
			check(meats[i].energy == energy[i], meats[i].getClass().getSimpleName() + "饱食值错误");
			check(meats[i].value() == values[i], meats[i].getClass().getSimpleName() + "价格错误");
		}
	}

	private static void testEffects() {
		boolean poisoned = false;
		for (int i = 0; i < 300 && !poisoned; i++) {
			Hero hero = hero(100);
			new Meat().doEat(hero);
			poisoned = hero.buff(Poison.class) != null;
		}
		check(poisoned, "生肉无法触发旧版十五分之一中毒");

		Hero hero = hero(100);
		hero.HP = 20;
		new DarkMeat().doEat(hero);
		check(hero.HP == 45, "腐蚀肉没有恢复四分之一最大生命");

		hero = hero(100);
		hero.lvl = 6;
		new EarthMeat().doEat(hero);
		check(hero.buff(Barkskin.class).level() == 18, "腌制扣肉树肤强度错误");

		hero = hero(100);
		new IceMeat().doEat(hero);
		check(hero.buff(Invisibility.class) != null, "冻肉片没有提供隐形");

		hero = hero(100);
		Buff.affect(hero, Poison.class).set(5f);
		Buff.affect(hero, Cripple.class, 5f);
		Buff.affect(hero, STRDown.class, 5f);
		Buff.affect(hero, Bleeding.class).set(5f);
		Buff.affect(hero, Drowsy.class, 5f);
		Buff.affect(hero, Slow.class, 5f);
		Buff.affect(hero, Vertigo.class, 5f);
		new ShockMeat().doEat(hero);
		check(hero.buff(Poison.class) == null && hero.buff(Cripple.class) == null
				&& hero.buff(STRDown.class) == null && hero.buff(Bleeding.class) == null
				&& hero.buff(Drowsy.class) == null && hero.buff(Slow.class) == null
				&& hero.buff(Vertigo.class) == null, "炸里脊没有清除旧版负面状态组");

		hero = hero(100);
		new FunnyFood().doEat(hero);
		check(hero.buff(FunnyBuff.class) != null, "滑稽料理没有附加滑稽状态");
	}

	private static void testHeapReactions() {
		checkReaction(Energy.FIRE, FireMeat.class);
		checkReaction(Energy.ICE, IceMeat.class);
		checkReaction(Energy.LIGHTNING, ShockMeat.class);
		checkReaction(Energy.DARK, DarkMeat.class);
		checkReaction(Energy.EARTH, EarthMeat.class);
		checkReaction(Energy.LIGHT, LightMeat.class);
	}

	private static void checkReaction(Energy energy, Class<?> expected) {
		Heap heap = new Heap();
		heap.items.add(new Meat().quantity(3));
		switch (energy) {
			case FIRE: heap.firehit(); break;
			case ICE: heap.icehit(); break;
			case LIGHTNING: heap.shockhit(); break;
			case DARK: heap.darkhit(); break;
			case EARTH: heap.earthhit(); break;
			case LIGHT: heap.lighthit(); break;
		}
		check(expected.isInstance(heap.peek()), energy + "没有生成正确的元素肉");
		check(heap.peek().quantity() == 3, energy + "加工时丢失了堆叠数量");
	}

	private static void testMonsterDrop() {
		check(new Rat().createLoot() instanceof Meat, "下水道老鼠没有掉落旧版生肉");
	}

	private static Hero hero(int health) {
		Hero hero = new Hero();
		hero.HTBoost = health - Hero.STARTING_HT;
		hero.updateHT(false);
		hero.HP = hero.HT;
		Dungeon.hero = hero;
		return hero;
	}

	private enum Energy { FIRE, ICE, LIGHTNING, DARK, EARTH, LIGHT }
	private static void check(boolean condition, String message) { if (!condition) throw new AssertionError(message); }
}
