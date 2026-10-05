/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.items.consum.food.WaterItem;
import pd.items.consum.food.completefood.FruitCandy;
import pd.items.consum.food.completefood.Gel;
import pd.items.consum.food.completefood.Mediummeat;
import pd.items.consum.food.completefood.MixPizza;
import pd.items.consum.food.completefood.NutCookie;
import pd.items.consum.food.fruit.Fruit;
import pd.items.consum.food.fusion.Nut;
import pd.items.consum.food.meatfood.MeatFood;
import pd.items.consum.food.staplefood.StapleFood;
import pd.items.consum.food.vegetable.Vegetable;
import pd.items.consum.medicine.Timepill2;
import pd.items.consum.potions.Potion;
import pd.items.consum.scrolls.Scroll;
import pd.items.consum.scrolls.ScrollOfIdentify;
import pd.items.consum.scrolls.ScrollOfMagicalInfusion;
import pd.items.consum.scrolls.ScrollOfRemoveCurse;
import pd.items.consum.stones.StoneOfAugmentation;
import pd.items.equipment.artifacts.AlienBag;
import pd.items.equipment.bombs.BuildBomb;
import pd.items.equipment.bombs.HugeBomb;
import pd.items.equipment.weapon.spammo.*;
import pd.items.quest.DarkGold;
import pd.messages.InlineText;
import pd.plants.*;
import render.utils.serialize.Reflection;

import java.util.ArrayList;

/**
 * SPSEXPD: 原铁砧的全部锻造公式，现已整体移入炼金釜（铁砧本身仅作装饰）。
 * 无匹配公式时返回 null，由炼金釜自身的废料兜底处理。
 */
public class IronMakerRecipes extends Recipe {

	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(IronMakerRecipes.class)
			.t("name", "锻造");
	}

	/** 复刻铁砧的锻造公式；无匹配时返回 null。 */
	@SuppressWarnings("unchecked")
	public static Item forge(ArrayList<Item> items) {
		int garbage = count(items, Garbage.class);
		int ore = count(items, StoneOre.class);
		int water = count(items, WaterItem.class);
		int equipment = count(items, EquipableItem.class);
		int seeds = count(items, Plant.Seed.class);
		int potions = count(items, Potion.class);
		int scrolls = count(items, Scroll.class);
		int meatfoods = count(items, MeatFood.class);

		if (items.size() == 5 && garbage + ore == 5) return Generator.random();
		if (items.size() == 5 && count(items, Nut.class) == 5) return new NutCookie(6);
		if (items.size() == 5 && ore == 4 && water == 1) return new Timepill2();
		if (items.size() == 5 && meatfoods == 1 && ore == 1
				&& count(items, StapleFood.class) == 1 && count(items, Fruit.class) == 1
				&& count(items, Vegetable.class) == 1) return new MixPizza(8);
		if (items.size() == 5 && potions == 1 && ore == 1 && scrolls == 1 && water == 1 && seeds == 1) {
			return new ScrollOfRemoveCurse();
		}
		if (items.size() == 4 && potions == 1 && ore == 1 && scrolls == 1 && water == 1) {
			return new ScrollOfIdentify();
		}
		if (items.size() == 3 && ore == 1 && seeds == 1 && scrolls == 1) return new BuildBomb();
		if (items.size() == 3 && count(items, BuildBomb.class) == 1 && seeds == 2) {
			return AlienBag.randomBombSupply();
		}
		if (items.size() == 3 && water == 1 && count(items, Fruit.class) == 1 && ore == 1) {
			return new FruitCandy(2);
		}
		if (items.size() == 4 && count(items, DarkGold.class) == 3 && ore == 1) return new PocketBall();
		if (items.size() == 2 && ore == 2) return new HeavyAmmo();
		if (items.size() == 2 && ore == 1) {
			if (count(items, NutPlant.Seed.class) == 1) return new WoodenAmmo();
			if (count(items, Firebloom.Seed.class) == 1) return new FireAmmo();
			if (count(items, Icecap.Seed.class) == 1) return new IceAmmo();
			if (count(items, Stormvine.Seed.class) == 1) return new StormAmmo();
			if (count(items, Sorrowmoss.Seed.class) == 1) return new MossAmmo();
			if (count(items, Blindweed.Seed.class) == 1) return new BlindAmmo();
			if (count(items, Starflower.Seed.class) == 1) return new StarAmmo();
			if (count(items, Dreamfoil.Seed.class) == 1) return new DreamAmmo();
			if (count(items, Dewcatcher.Seed.class) == 1) return new DewAmmo();
			if (count(items, Sungrass.Seed.class) == 1) return new SunAmmo();
			if (count(items, Fadeleaf.Seed.class) == 1) return new SandAmmo();
			if (count(items, Seedpod.Seed.class) == 1) return new GoldAmmo();
			if (count(items, Rotberry.Seed.class) == 1 || count(items, Freshberry.Seed.class) == 1) return new RotAmmo();
			if (count(items, Earthroot.Seed.class) == 1) return new ThornAmmo();
			if (count(items, BlandfruitBush.Seed.class) == 1) return new EmptyAmmo();
			if (count(items, ReNepenth.Seed.class) == 1) return new EvolveAmmo();
			if (count(items, StarEater.Seed.class) == 1) return new BattleAmmo();
		}
		if (items.size() == 2 && count(items, BuildBomb.class) == 2) return new HugeBomb();
		if (items.size() == 2 && count(items, StoneOfAugmentation.class) == 1
				&& count(items, Stylus.class) == 1) return new GreatRune();
		if (items.size() == 2 && water == 1 && meatfoods == 1) return new Mediummeat();
		if (items.size() == 1 && count(items, ScrollOfMagicalInfusion.class) == 1) return new GreatRune();
		if (items.size() == 1 && count(items, Gel.class) == 1) return new Torch();
		if (items.size() == 2 && equipment == 2 && items.get(0).getClass() == items.get(1).getClass()) {
			Item result = Reflection.newInstance((Class<? extends Item>)items.get(0).getClass());
			if (result != null && result.isUpgradable()) {
				result.level(items.get(0).level() + 1).identify();
				result.cursed = false;
				result.cursedKnown = true;
				return result;
			}
			return new Garbage(2);
		}
		if (equipment == 1 && water > 0 && equipment + water == items.size()) {
			Item source = first(items, EquipableItem.class);
			if (source != null && source.isUpgradable() && render.utils.math.Random.Int(100) < water * 15) {
				Item result = Reflection.newInstance((Class<? extends Item>)source.getClass());
				if (result == null) return new Garbage();
				result.level(source.level() + 1).identify();
				result.cursed = false;
				result.cursedKnown = true;
				return result;
			}
			return new Garbage();
		}
		if (items.size() == 5 && seeds == 5) return new Garbage(3);
		if (items.size() == 2 && seeds == 2) return new Garbage();
		if (items.size() == 1 && seeds == 1) return new GreenDewdrop();
		if (items.size() == 1 && equipment == 1) return new Garbage(2);
		return null;
	}

	@Override
	public boolean testIngredients(ArrayList<Item> ingredients) {
		return !ingredients.isEmpty() && forge(ingredients) != null;
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
		return forge(ingredients);
	}

	@Override
	public Item sampleOutput(ArrayList<Item> ingredients) {
		return forge(ingredients);
	}

	private static int count(ArrayList<Item> items, Class<?> type) {
		int result = 0;
		for (Item item : items) if (type.isInstance(item)) result++;
		return result;
	}

	private static Item first(ArrayList<Item> items, Class<?> type) {
		for (Item item : items) if (type.isInstance(item)) return item;
		return null;
	}
}
