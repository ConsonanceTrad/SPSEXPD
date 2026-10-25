/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package pd.items;

import pd.items.consum.food.processed.Dough;
import pd.items.consum.food.processed.Mince;
import pd.items.consum.food.processed.Salt;
import pd.items.consum.food.processed.Syrup;
import pd.items.consum.food.processed.PickledMeat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.items.consum.food.Food;
import pd.items.consum.food.MeatPie;
import pd.items.consum.food.WaterItem;
import pd.items.consum.food.completefood.Chickennugget;
import pd.items.consum.food.completefood.Chocolate;
import pd.items.consum.medicine.Foamedbeverage;
import pd.items.consum.food.completefood.Frenchfries;
import pd.items.consum.food.completefood.Gel;
import pd.items.consum.food.completefood.Hamburger;
import pd.items.consum.food.completefood.Herbmeat;
import pd.items.consum.food.completefood.HoneyGel;
import pd.items.consum.food.completefood.HoneyWater;
import pd.items.consum.food.completefood.Honeymeat;
import pd.items.consum.food.completefood.Honeyrice;
import pd.items.consum.food.completefood.Icecream;
import pd.items.consum.food.completefood.Kebab;
import pd.items.consum.food.completefood.Meatroll;
import pd.items.consum.food.completefood.MoonCake;
import pd.items.consum.food.completefood.NutCake;
import pd.items.consum.food.completefood.PerfectFood;
import pd.items.consum.food.completefood.PetFood;
import pd.items.consum.food.completefood.Porksoup;
import pd.items.consum.food.completefood.RiceGruel;
import pd.items.consum.food.completefood.Sishimi;
import pd.items.consum.medicine.TimePill;
import pd.items.consum.food.completefood.Vegetablekebab;
import pd.items.consum.food.completefood.Vegetableroll;
import pd.items.consum.food.completefood.Vegetablesoup;
import pd.items.consum.food.completefood.ZongZi;
import pd.items.consum.food.fruit.Fruit;
import pd.items.consum.food.fusion.Nut;
import pd.items.consum.food.meatfood.MeatFood;
import pd.items.consum.food.staplefood.OverpricedRation;
import pd.items.consum.food.staplefood.StapleFood;
import pd.items.consum.food.vegetable.NutVegetable;
import pd.items.consum.food.vegetable.Vegetable;
import pd.items.consum.scrolls.Scroll;
import pd.plants.Icecap;
import pd.plants.Plant;

/**
 * SPSEXPD: 食材组合 / 简餐烹饪配方（docs/alchemy-rework.md §8）。
 *
 * <p>原先把这些「食材 → 成品菜」条目挂在炼金釜的固定配方表里，等于做什么菜都要进炼金界面、
 * 都要花能量。现在按工具分流：
 * <ul>
 *   <li>{@link #FILL} 分装灌液机——**凡含水的配方**（粥、汤、生鱼片、蜂蜜水、气泡饮料、沙拉、冰淇淋…）</li>
 *   <li>{@link #PAN} 平底煎锅——**需要加热的无水配方**（汉堡、烤串、炸物、蜜渍、烘焙、粽子…），
 *       另含「单份食材随手煎」的血源风路径；平底锅每做一道菜额外消耗 1 个火种</li>
 *   <li>{@link #BOARD} 菜板——**无需加热、也无需水的冷食组合**（坚果拼盘、蜜胶…）</li>
 * </ul>
 */
public final class SpsFoodPrepRecipes {

	private SpsFoodPrepRecipes() {}

	/** 分装灌液机（含水配方）。 */
	public static final int FILL = 0;
	/** 平底煎锅（需加热）。 */
	public static final int PAN = 1;
	/** 菜板（无水冷食）。 */
	public static final int BOARD = 2;

	/** WndCrafting 的模式号 → 本表的 kind；不属于食物加工的返回 -1。 */
	public static int kindForMode(int mode) {
		switch (mode) {
			case 2:  return FILL;   // MODE_FILL
			case 3:  return PAN;    // MODE_COOK
			case 4:  return BOARD;  // MODE_BOARD
			default: return -1;
		}
	}

	//*** 配方表

	private static final List<Recipe> FILL_RECIPES = Arrays.asList(
			//SPSEXPD: 语义化——粥/汤/腌菜都走「原料 + 水 + 盐」，肉用肉馅、甜味用甜酱
			SpsAlchemyRecipes.recipe(2, RiceGruel.class, StapleFood.class, WaterItem.class, Salt.class),
			SpsAlchemyRecipes.recipe(Porksoup.class, Mince.class, WaterItem.class, Salt.class),
			SpsAlchemyRecipes.recipe(Vegetablesoup.class, Vegetable.class, WaterItem.class, Salt.class),
			SpsAlchemyRecipes.recipe(Sishimi.class, MeatFood.class, WaterItem.class, Salt.class),
			//饮品
			SpsAlchemyRecipes.recipe(HoneyWater.class, Syrup.class, WaterItem.class),
			SpsAlchemyRecipes.recipe(5, Foamedbeverage.class, StoneOre.class, WaterItem.class, WaterItem.class, Plant.Seed.class, Fruit.class),
			SpsAlchemyRecipes.recipe(Icecream.class, Syrup.class, WaterItem.class, Icecap.Seed.class),
			//含水菜
			new SpsAlchemyRecipes.FruitSalad(),
			SpsAlchemyRecipes.recipe(PerfectFood.class, Vegetable.class, StoneOre.class, Dough.class, WaterItem.class, Fruit.class),
			SpsAlchemyRecipes.recipe(TimePill.class, StoneOre.class, StoneOre.class, StoneOre.class, StoneOre.class, WaterItem.class),
			SpsAlchemyRecipes.recipe(Crystalnucleus.class, StoneOre.class, StoneOre.class, StoneOre.class, WaterItem.class, Plant.Seed.class),
			SpsAlchemyRecipes.recipe(Gel.class, StoneOre.class, WaterItem.class),
			SpsAlchemyRecipes.recipe(PetFood.class, Nut.class, Mince.class, WaterItem.class),
			SpsAlchemyRecipes.BLANDFRUIT_TO_RATION
	);

	private static final List<Recipe> PAN_RECIPES = Arrays.asList(
			//SPSEXPD: 语义化——面食用面团、肉菜用肉馅、调味用盐/甜酱，其中腌肉与甜酱本身就是中间食物
			SpsAlchemyRecipes.recipe(Hamburger.class, Dough.class, Mince.class, Vegetable.class, Salt.class),
			SpsAlchemyRecipes.recipe(Kebab.class, Mince.class, Vegetable.class, Salt.class),
			SpsAlchemyRecipes.recipe(Vegetablekebab.class, Vegetable.class, Vegetable.class, Salt.class),
			SpsAlchemyRecipes.recipe(Chickennugget.class, Mince.class, Nut.class, Salt.class),
			SpsAlchemyRecipes.recipe(Frenchfries.class, Vegetable.class, Nut.class, Salt.class),
			SpsAlchemyRecipes.recipe(Meatroll.class, Dough.class, Mince.class, Salt.class),
			SpsAlchemyRecipes.recipe(Vegetableroll.class, Dough.class, Vegetable.class, Salt.class),
			SpsAlchemyRecipes.recipe(Honeymeat.class, PickledMeat.class, Syrup.class),
			SpsAlchemyRecipes.recipe(Honeyrice.class, StapleFood.class, Syrup.class),
			SpsAlchemyRecipes.recipe(Herbmeat.class, Mince.class, Plant.Seed.class),
			SpsAlchemyRecipes.recipe(Chocolate.class, Nut.class, Nut.class, Nut.class, Syrup.class),
			SpsAlchemyRecipes.recipe(OverpricedRation.class, Nut.class, Nut.class, Salt.class),
			SpsAlchemyRecipes.recipe(NutCake.class, Dough.class, Nut.class, Syrup.class),
			SpsAlchemyRecipes.recipe(MoonCake.class, Dough.class, Nut.class, Nut.class),
			SpsAlchemyRecipes.recipe(ZongZi.class, StapleFood.class, Mince.class, Salt.class),
			new SingleIngredientCook()
	);

	private static final List<Recipe> BOARD_RECIPES = Arrays.asList(
			SpsAlchemyRecipes.recipe(NutVegetable.class, Nut.class),
			SpsAlchemyRecipes.recipe(HoneyGel.class, pd.items.consum.food.Honey.class, Gel.class)
	);

	/** 取某个工具能用的食物配方。 */
	public static ArrayList<Recipe> forKind(int kind) {
		ArrayList<Recipe> out = new ArrayList<>();
		switch (kind) {
			case FILL:  out.addAll(FILL_RECIPES); break;
			case PAN:   out.addAll(PAN_RECIPES); break;
			case BOARD: out.addAll(BOARD_RECIPES); break;
			default: break;
		}
		return out;
	}

	/** 这份配方是否属于「食物加工」（炼金釜要排除它们）。 */
	public static boolean isFoodPrep(Recipe recipe) {
		return FILL_RECIPES.contains(recipe)
				|| PAN_RECIPES.contains(recipe)
				|| BOARD_RECIPES.contains(recipe);
	}

	/** SPSEXPD: 平底锅对「单份可烹制食材」的兜底——沿用血源风的烹饪技巧阈值。 */
	public static final class SingleIngredientCook extends Recipe {

		@Override
		public boolean testIngredients(ArrayList<Item> ingredients) {
			return ingredients.size() == 1
					&& ingredients.get(0) instanceof Food
					&& ((Food) ingredients.get(0)).canBeCook;
		}

		@Override
		public int cost(ArrayList<Item> ingredients) {
			return 0;
		}

		@Override
		public Item brew(ArrayList<Item> ingredients) {
			if (!testIngredients(ingredients)) return null;
			Food ingredient = (Food) ingredients.get(0);
			Hero hero = Dungeon.hero;
			int skill = hero == null ? 0 : hero.cookingSkill;
			Item dish = pd.items.equipment.weapon.melee.special.MeleePan.dishFor(ingredient, skill);
			if (dish == null) return null;
			ingredient.quantity(ingredient.quantity() - 1);
			if (hero != null && hero.cookingSkill < 100) hero.cookingSkill++;
			return dish;
		}

		@Override
		public Item sampleOutput(ArrayList<Item> ingredients) {
			if (!testIngredients(ingredients)) return null;
			Hero hero = Dungeon.hero;
			int skill = hero == null ? 0 : hero.cookingSkill;
			return pd.items.equipment.weapon.melee.special.MeleePan.dishFor((Food) ingredients.get(0), skill);
		}
	}

	//避免与本文件顶部的内联常量混淆
}
