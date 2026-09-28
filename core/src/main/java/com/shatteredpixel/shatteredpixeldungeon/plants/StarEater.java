package com.shatteredpixel.shatteredpixeldungeon.plants;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.UpgradeEatBall;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class StarEater extends Plant {
	{ image = 15; seedClass = Seed.class; }
	@Override public void activate(Char ch) {
		Dungeon.level.drop(new UpgradeEatBall(), pos).sprite.drop();
		Dungeon.level.drop(Generator.random(Generator.Category.BERRY), pos).sprite.drop();
	}
	public static class Seed extends Plant.Seed {
		{ image = ItemSpriteSheet.SPS_SEED_STAREATER; plantClass = StarEater.class; explantClass = ExStarEater.class; }
	}
	public static class ExStarEater extends SpsFruitBush {
		{ image = 15; harvestCount = 2; harvestClass = UpgradeEatBall.class; }
	}
}
