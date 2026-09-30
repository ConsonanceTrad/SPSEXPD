package pd.plants;

import pd.Dungeon;
import pd.actors.Char;
import pd.items.Generator;
import pd.items.UpgradeEatBall;
import pd.sprites.ItemSpriteSheet;

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
