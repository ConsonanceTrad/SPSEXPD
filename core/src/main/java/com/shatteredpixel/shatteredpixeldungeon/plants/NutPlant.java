package com.shatteredpixel.shatteredpixeldungeon.plants;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.food.fusion.Nut;
import com.shatteredpixel.shatteredpixeldungeon.items.food.fruit.Cherry;
import com.shatteredpixel.shatteredpixeldungeon.items.food.fruit.Strawberry;
import com.shatteredpixel.shatteredpixeldungeon.items.food.vegetable.NutVegetable;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.arrows.NutFruit;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

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
