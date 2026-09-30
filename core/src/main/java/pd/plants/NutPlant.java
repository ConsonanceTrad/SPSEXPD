package pd.plants;

import pd.Dungeon;
import pd.actors.Char;
import pd.items.food.fusion.Nut;
import pd.items.food.fruit.Cherry;
import pd.items.food.fruit.Strawberry;
import pd.items.food.vegetable.NutVegetable;
import pd.items.weapon.missiles.arrows.NutFruit;
import pd.sprites.ItemSpriteSheet;
import render.utils.Random;

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
		{ image = ItemSpriteSheet.SPS_SEED_DUNGEONNUT; plantClass = NutPlant.class; explantClass = ExNutPlant.class; }
	}
	public static class ExNutPlant extends SpsFruitBush {
		{ image = 17; harvestCount = 3; harvestClass = NutFruit.class; }
	}
}
