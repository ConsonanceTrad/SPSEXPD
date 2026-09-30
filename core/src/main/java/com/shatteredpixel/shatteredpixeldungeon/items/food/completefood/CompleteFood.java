/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.food.completefood;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.watabou.utils.Random;

import java.util.HashMap;
import java.util.Map;

/** Common base for SPS-PD's prepared foods. */
public class CompleteFood extends Food {

	//号角值按食物类名归类（类名即语义：改名需同步本表），未列出的默认 3
	private static final Map<String, Integer> HORN_VALUES = new HashMap<>();
	static {
		for (String name : new String[]{"Gel", "HarmPoop"}) HORN_VALUES.put(name, 0);
		for (String name : new String[]{"FishPetFood", "FruitCandy", "NutCookie", "PetFood"}) HORN_VALUES.put(name, 1);
		for (String name : new String[]{"FoodFans", "Frenchfries", "HoneyGel", "HoneyWater",
				"Meatroll", "MixPizza", "MoonCake", "RiceGruel",
				"Vegetablekebab", "Vegetableroll"}) HORN_VALUES.put(name, 2);
		for (String name : new String[]{"Chocolate", "ZongZi"}) HORN_VALUES.put(name, 5);
		HORN_VALUES.put("Hamburger", 6);
		HORN_VALUES.put("PerfectFood", 10);
	}

	{
		hornValue = HORN_VALUES.getOrDefault(getClass().getSimpleName(), 3);
	}

	@Override
	protected void satisfy(Hero hero) {
		super.satisfy(hero);
		doEat(hero);
	}

	protected void doEat(Hero hero) {
		// Most prepared foods add an effect; RiceGruel intentionally does not.
	}

	protected static void increaseMaxHealth(Hero hero, int minimum, int maximumExclusive) {
		hero.HTBoost += Random.Int(minimum, maximumExclusive);
		hero.updateHT(true);
	}

	protected static void heal(Hero hero, int amount) {
		hero.HP = Math.min(hero.HT, hero.HP + Math.max(0, amount));
	}
}
