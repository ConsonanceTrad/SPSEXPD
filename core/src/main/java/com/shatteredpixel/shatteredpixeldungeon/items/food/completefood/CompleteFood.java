/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.food.completefood;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.watabou.utils.Random;

/** Common base for SPS-PD's prepared foods. */
public class CompleteFood extends Food {

	{
		switch (getClass().getSimpleName()) {
			case "Gel": case "HarmPoop":
				hornValue = 0;
				break;
			case "FishPetFood": case "FruitCandy": case "NutCookie": case "PetFood":
				hornValue = 1;
				break;
			case "FoodFans": case "Frenchfries": case "HoneyGel": case "HoneyWater":
			case "Meatroll": case "MixPizza": case "MoonCake": case "RiceGruel":
			case "Vegetablekebab": case "Vegetableroll":
				hornValue = 2;
				break;
			case "Chocolate": case "ZongZi":
				hornValue = 5;
				break;
			case "Hamburger":
				hornValue = 6;
				break;
			case "PerfectFood":
				hornValue = 10;
				break;
			default:
				hornValue = 3;
		}
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
