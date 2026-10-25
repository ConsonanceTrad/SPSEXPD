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

import pd.ShatteredPixelDungeon;
import pd.items.consum.food.vegetable.FoodVegetable;
import pd.items.equipment.bombs.Bomb;
import pd.items.equipment.bags.Bag;
import pd.items.consum.food.MeatPie;
import pd.items.consum.food.StewedMeat;
import pd.items.consum.potions.Potion;
import pd.items.consum.potions.brews.AquaBrew;
import pd.items.consum.potions.brews.BlizzardBrew;
import pd.items.consum.potions.brews.CausticBrew;
import pd.items.consum.potions.brews.InfernalBrew;
import pd.items.consum.potions.brews.ShockingBrew;
import pd.items.consum.potions.brews.UnstableBrew;
import pd.items.consum.potions.elixirs.ElixirOfAquaticRejuvenation;
import pd.items.consum.potions.elixirs.ElixirOfArcaneArmor;
import pd.items.consum.potions.elixirs.ElixirOfDragonsBlood;
import pd.items.consum.potions.elixirs.ElixirOfFeatherFall;
import pd.items.consum.potions.elixirs.ElixirOfHoneyedHealing;
import pd.items.consum.potions.elixirs.ElixirOfIcyTouch;
import pd.items.consum.potions.elixirs.ElixirOfMight;
import pd.items.consum.potions.elixirs.ElixirOfToxicEssence;
import pd.items.consum.potions.exotic.ExoticPotion;
import pd.items.consum.scrolls.Scroll;
import pd.items.consum.spells.Alchemize;
import pd.items.consum.spells.BeaconOfReturning;
import pd.items.consum.spells.CurseInfusion;
import pd.items.consum.spells.MagicalInfusion;
import pd.items.consum.spells.PhaseShift;
import pd.items.consum.spells.ReclaimTrap;
import pd.items.consum.spells.Recycle;
import pd.items.consum.spells.SummonElemental;
import pd.items.consum.spells.TelekineticGrab;
import pd.items.consum.spells.UnstableSpell;
import pd.items.consum.spells.WildEnergy;
import pd.items.equipment.trinkets.Trinket;
import pd.items.equipment.trinkets.TrinketCatalyst;
import pd.items.equipment.wands.Wand;
import pd.items.equipment.weapon.SpiritBow;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import render.utils.serialize.Reflection;

import java.util.ArrayList;

public abstract class Recipe {
	
	public abstract boolean testIngredients(ArrayList<Item> ingredients);
	
	public abstract int cost(ArrayList<Item> ingredients);
	
	public abstract Item brew(ArrayList<Item> ingredients);
	
	public abstract Item sampleOutput(ArrayList<Item> ingredients);
	
	//subclass for the common situation of a recipe with static inputs and outputs
	public static abstract class SimpleRecipe extends Recipe {
		
		//*** These elements must be filled in by subclasses
		protected Class<?extends Item>[] inputs; //each class should be unique
		protected int[] inQuantity;
		
		protected int cost;
		
		protected Class<?extends Item> output;
		protected int outQuantity;
		//***
		
		//gets a simple list of items based on inputs
		public ArrayList<Item> getIngredients() {
			ArrayList<Item> result = new ArrayList<>();
			for (int i = 0; i < inputs.length; i++) {
				Item ingredient = Reflection.newInstance(inputs[i]);
				ingredient.quantity(inQuantity[i]);
				result.add(ingredient);
			}
			return result;
		}
		
		@Override
		public boolean testIngredients(ArrayList<Item> ingredients) {
			
			int[] needed = inQuantity.clone();
			
			for (Item ingredient : ingredients){
				if (!ingredient.isIdentified()) return false;
				for (int i = 0; i < inputs.length; i++){
					if (ingredient.getClass() == inputs[i]){
						needed[i] -= ingredient.quantity();
						break;
					}
				}
			}
			
			for (int i : needed){
				if (i > 0){
					return false;
				}
			}
			
			return true;
		}
		
		public int cost(ArrayList<Item> ingredients){
			return cost;
		}
		
		@Override
		public Item brew(ArrayList<Item> ingredients) {
			if (!testIngredients(ingredients)) return null;
			
			int[] needed = inQuantity.clone();
			
			for (Item ingredient : ingredients){
				for (int i = 0; i < inputs.length; i++) {
					if (ingredient.getClass() == inputs[i] && needed[i] > 0) {
						if (needed[i] <= ingredient.quantity()) {
							ingredient.quantity(ingredient.quantity() - needed[i]);
							needed[i] = 0;
						} else {
							needed[i] -= ingredient.quantity();
							ingredient.quantity(0);
						}
					}
				}
			}
			
			//sample output and real output are identical in this case.
			return sampleOutput(null);
		}
		
		//ingredients are ignored, as output doesn't vary
		public Item sampleOutput(ArrayList<Item> ingredients){
			try {
				Item result = Reflection.newInstance(output);
				result.quantity(outQuantity);
				return result;
			} catch (Exception e) {
				ShatteredPixelDungeon.reportException( e );
				return null;
			}
		}
	}
	
	
	//*******
	// Static members
	//*******

	/**
	 * SPSEXPD: 新炼金框架的公式表（docs/alchemy-rework.md）。
	 * 惰性初始化，避免与 Potion/ExoticPotion 的类初始化互相牵制。
	 */
	private static Recipe[] brewRecipes = null;

	private static Recipe[] brewRecipes() {
		if (brewRecipes == null) {
			brewRecipes = SpsBrewRecipes.all().toArray(new Recipe[0]);
		}
		return brewRecipes;
	}

	private static Recipe[] variableRecipes = new Recipe[]{
			//SPSEXPD: 果实→药剂的直换与「大果→合剂」直换已删除（docs/alchemy-rework.md 裁决 #1A），
			//改由 SpsBrewRecipes 的粗制/精制/合剂工艺产出；FruitToPotion 仅保留作映射表数据源。
			new IronMakerRecipes()
	};
	
	private static Recipe[] oneIngredientRecipes = new Recipe[]{
		new Scroll.ScrollToStone(),
		//SPSEXPD: 合剂/魔药/秘药的一步直换全部撤下（docs/alchemy-rework.md §7）——
		//合剂与魔药改由「主材 + 本局两味」产出，秘药也并入同一体系，
		//所以 ExoticPotion.PotionToExotic、各 Brew.Recipe、各 ElixirOf*.Recipe 不再注册。
		new ArcaneResin.Recipe(),
		new LiquidMetal.Recipe(),
		new MagicalInfusion.Recipe(),
		new BeaconOfReturning.Recipe(),
		new PhaseShift.Recipe(),
		new Recycle.Recipe(),
		new TelekineticGrab.Recipe(),
		new SummonElemental.Recipe(),
		new StewedMeat.oneMeat(),
		new TrinketCatalyst.Recipe(),
		new Trinket.UpgradeTrinket()
	};
	
	private static Recipe[] twoIngredientRecipes = new Recipe[]{
		new Bomb.EnhanceBomb(),
		//SPSEXPD: CausticBrew / UnstableBrew / 各 ElixirOf* 的直换同样撤下（见上方说明）
		new UnstableSpell.Recipe(),
		new Alchemize.Recipe(),
		new CurseInfusion.Recipe(),
		new ReclaimTrap.Recipe(),
		new WildEnergy.Recipe(),
		new StewedMeat.twoMeat()
	};
	
	private static Recipe[] threeIngredientRecipes = new Recipe[]{
		new StewedMeat.threeMeat(),
		new MeatPie.Recipe()
	};
	
	public static ArrayList<Recipe> findRecipes(ArrayList<Item> ingredients){
		return findRecipes(ingredients, BrewRecipe.Slots.of(ingredients));
	}

	/** SPSEXPD: 带语义槽的匹配入口——炼金釜按「主材/辅材/水/火源」的真实槽位喂入。 */
	public static ArrayList<Recipe> findRecipes(ArrayList<Item> ingredients, BrewRecipe.Slots slots){

		ArrayList<Recipe> result = new ArrayList<>();
		Recipe spsRecipe = SpsAlchemyRecipes.findRecipe(ingredients);
		if (spsRecipe != null) {
			result.add(spsRecipe);
			return result;
		}

		//SPSEXPD: 新炼金框架（主材/辅材/水/火源 + 成功率）
		for (Recipe recipe : brewRecipes()){
			boolean matched = recipe instanceof BrewRecipe
					? ((BrewRecipe) recipe).matches(slots)
					: recipe.testIngredients(ingredients);
			if (matched){
				result.add(recipe);
			}
		}
		if (!result.isEmpty()){
			return result;
		}

		for (Recipe recipe : variableRecipes){
			if (recipe.testIngredients(ingredients)){
				result.add(recipe);
			}
		}

		if (ingredients.size() == 1){
			for (Recipe recipe : oneIngredientRecipes){
				if (recipe.testIngredients(ingredients)){
					result.add(recipe);
				}
			}
			
		} else if (ingredients.size() == 2){
			for (Recipe recipe : twoIngredientRecipes){
				if (recipe.testIngredients(ingredients)){
					result.add(recipe);
				}
			}
			
		} else if (ingredients.size() == 3){
			for (Recipe recipe : threeIngredientRecipes){
				if (recipe.testIngredients(ingredients)){
					result.add(recipe);
				}
			}
		}
		//SPSEXPD: 原为「无匹配则产出垃圾」兜底，已随垃圾物品一并取消——不匹配就是没有可用配方
		
		return result;
	}
	
	public static boolean usableInRecipe(Item item){
		//SPSEXPD: 任务/剧情道具与角色专属道具不能投入炼金釜
		if (isSpecialItem(item)) return false;
		//SPSEXPD: 包裹袋（绒布袋/卷轴筒/药剂挎带/草靶子/竹背篓等）是容器，不是炼金材料
		if (item instanceof Bag) return false;
		//SPSEXPD: 灵能弓（含灵能袖箭等子类）是女猎手专属永久武器，不参与炼金
		//（isSpecialItem 里已排除一次，这里再拦一道，避免以后调整特殊道具清单时漏掉）
		if (item instanceof SpiritBow) return false;
		//SPSEXPD: 食材蔬菜（胡萝卜/莴苣/松露）只是食物，不参与炼金
		//——例外：能直接榨汁的这几种（松露/莴苣/苹果）必须能放进炼金釜，见 SimpleJuiceBrew
		if (item instanceof pd.items.consum.food.vegetable.Truffles
				|| item instanceof pd.items.consum.food.vegetable.Lettuce
				|| item instanceof pd.items.consum.food.vegetable.Apple) return true;
		if (item instanceof FoodVegetable) return false;
		if (item instanceof EquipableItem){
			//SPSEXPD: 投掷果实（SpsFruit 家族）是炼金主材，但它继承自 MissileWeapon（EquipableItem），
			//且 cursedKnown 默认为 false —— 若走下面的装备判定会被当成"未鉴定装备"而整类不可选
			//（表现为「果子放不进炼金槽位」）。果实没有鉴定/诅咒概念，直接按材料处理。
			if (item instanceof MissileWeapon && ((MissileWeapon) item).isFruit()) return !item.cursed;
			//SPSEXPD: 除特殊道具外的装备均可投入（不再限于可升级的投掷武器）
			return item.cursedKnown && !item.cursed;
		} else if (item instanceof Wand) {
			return item.cursedKnown && !item.cursed;
		} else {
			//other items can be unidentified, but not cursed
			return !item.cursed;
		}
	}

	/**
	 * SPSEXPD: 特殊道具——任务/剧情道具、图鉴与挑战书页、钥匙，
	 * 以及角色专属道具（鞋子、神圣护盾、技能书、皮肤专属初始装备等）。
	 * 这些物品不参与炼金。
	 */
	public static boolean isSpecialItem(Item item){
		String pkg = item.getClass().getPackageName();
		//任务与剧情
		if (pkg.startsWith("pd.items.quest")) return true;
		//图鉴页/日志页/挑战纸片/钥匙
		if (pkg.startsWith("pd.items.specific")) return true;
		//护甲配件包：升格为可反复使用的换皮道具后不再参与炼金
		if (item instanceof ArmorKit) return true;
		//SPSEXPD: 灵能弓是女猎手的专属永久武器，不参与炼金
		if (item instanceof SpiritBow) return true;
		//BOSS 钥匙与剧情道具
		if (item instanceof SpsBossKey || item instanceof TreasureMap || item instanceof TengusMask
				|| item instanceof KingsCrown || item instanceof Amulet || item instanceof DolyaSlate
				|| item instanceof Triforce || item instanceof TriforcePiece
				|| item instanceof TriforceOfCourage || item instanceof TriforceOfPower
				|| item instanceof TriforceOfWisdom || item instanceof SoulCollect
				|| item instanceof ChallengeBook || item instanceof KnowledgeBook
				|| item instanceof PotKey || item instanceof ShadowEaterKey
				|| item instanceof BossRush || item instanceof Playericon
				|| item instanceof BrokenSeal) return true;
		//角色专属道具（鞋子/神圣护盾/侵蚀核心/机械口袋/特权道具/技能书等）
		if (pkg.equals("pd.items.misc")) return true;
		if (pkg.startsWith("pd.items.skills")) return true;
		if (item instanceof SkillBook) return true;
		//皮肤专属初始装备
		if (pkg.endsWith(".melee.start") || pkg.endsWith(".specialarmor")) return true;
		return false;
	}
}
