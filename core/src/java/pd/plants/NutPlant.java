package pd.plants;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Char;
import pd.items.consum.food.fruit.Cherry;
import pd.items.consum.food.fruit.Strawberry;
import pd.items.consum.food.fusion.Nut;
import pd.items.consum.food.vegetable.NutVegetable;
import pd.items.equipment.weapon.missiles.arrows.NutFruit;
import render.utils.math.Random;

public class NutPlant extends Plant {
	{ image = 17; seedClass = Seed.class; }
	@Override public void activate(Char ch) {
		if (Random.Int(5) == 1) {
			if (Random.Int(2) == 1) Dungeon.level.drop(new Strawberry(), pos).sprite.drop();
			else Dungeon.level.drop(new Cherry(), pos).sprite.drop();
		} else {
			Dungeon.level.drop(new Nut(), pos).sprite.drop();
		}
		Dungeon.level.drop(new NutVegetable(), pos).sprite.drop();
	}
	public static class Seed extends Plant.Seed {
		{ image = SpecificPlaceHolderDict.SOMETHING_0; plantClass = NutPlant.class; explantClass = ExNutPlant.class; }
	}
	public static class ExNutPlant extends SpsFruitBush {
		{ image = 17; harvestCount = 3; harvestClass = NutFruit.class; }
	}
}
