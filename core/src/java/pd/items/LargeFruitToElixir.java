/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.items.consum.potions.Potion;
import pd.items.consum.potions.elixirs.*;
import pd.items.equipment.weapon.missiles.arrows.*;
import render.utils.serialize.Reflection;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * SPSEXPD: 1 个大型果实 → 对应秘药（取代旧的「3 个同种大果」与随机炼药）。
 * 无法自然对应的几种大果暂时指向"占位秘药"（无效果），后续再补足。
 */
public class LargeFruitToElixir extends Recipe {

	public static final int COUNT = 1;

	/** 大型果实类 → 对应的合剂类（吞星花对应强酸药剂的合剂升级，其余仍指秘药系）。 */
	public static final LinkedHashMap<Class<? extends Item>, Class<? extends Potion>> types = new LinkedHashMap<>();

	static {
		types.put(LargeHealFruit.class,        ElixirOfHoneyedHealing.class);
		types.put(LargeFreshFruit.class,       ElixirOfAquaticRejuvenation.class);
		types.put(LargeRotFruit.class,         ElixirOfToxicEssence.class);
		types.put(LargeFireFruit.class,        ElixirOfDragonsBlood.class);
		types.put(LargeIceFruit.class,         ElixirOfIcyTouch.class);
		types.put(LargeShockFruit.class,       ElixirOfArcaneArmor.class);
		types.put(LargeRootFruit.class,        ElixirOfArcaneArmor.class);
		types.put(LargeToxicFruit.class,       ElixirOfToxicEssence.class);
		types.put(LargeGlassFruit.class,       ElixirOfArcaneArmor.class);
		types.put(LargeBlindFruit.class,       ElixirOfBlinding.class);
		types.put(LargeCharmFruit.class,       ElixirOfCharm.class);
		types.put(LargeSmokeFruit.class,       ElixirOfFeatherFall.class);
		types.put(LargeNutFruit.class,         ElixirOfMight.class);
		types.put(LargeDewFruit.class,         ElixirOfAquaticRejuvenation.class);
		types.put(LargeSeedFruit.class,        ElixirOfSeeds.class);
		//SPSEXPD: 吞星花对应的合剂——强酸药剂的合剂升级（原误指根骨秘药）
		types.put(LargeStarEaterFruit.class,   pd.items.consum.potions.exotic.PotionOfAcidFeast.class);
		types.put(LargeStarFruit.class,        ElixirOfStars.class);
		types.put(LargeTransmuteFruit.class,   ElixirOfTransmutation.class);
		types.put(LargeFlavorlessFruit.class,  ElixirOfBlandness.class);
		types.put(LargeSwiftFruit.class,       ElixirOfFeatherFall.class);
	}

	/** 沿继承链查找大型果实对应的合剂。 */
	public static Class<? extends Potion> elixirFor(Item fruit) {
		for (Class<?> type = fruit.getClass(); type != null; type = type.getSuperclass()) {
			Class<? extends Potion> elixir = types.get(type);
			if (elixir != null) return elixir;
		}
		return null;
	}

	@Override
	public boolean testIngredients(ArrayList<Item> ingredients) {
		if (ingredients.size() != COUNT) return false;
		Class<? extends Potion> target = null;
		for (Item ingredient : ingredients) {
			//SPSEXPD: 只接受大型果实（其自身类带映射，且属于果实体系）
			Class<? extends Potion> elixir = elixirFor(ingredient);
			if (elixir == null) return false;
			if (!Potion.FruitToPotion.isLarge(ingredient)) return false;
			if (target == null) target = elixir;
			else if (target != elixir) return false;
		}
		return target != null;
	}

	@Override
	public int cost(ArrayList<Item> ingredients) {
		return 0;
	}

	@Override
	public Item brew(ArrayList<Item> ingredients) {
		if (!testIngredients(ingredients)) return null;
		for (Item ingredient : ingredients) {
			ingredient.quantity(ingredient.quantity() - 1);
		}
		return Reflection.newInstance(elixirFor(ingredients.get(0)));
	}

	@Override
	public Item sampleOutput(ArrayList<Item> ingredients) {
		if (ingredients.isEmpty()) return null;
		Class<? extends Potion> type = elixirFor(ingredients.get(0));
		return type == null ? null : Reflection.newInstance(type);
	}
}
