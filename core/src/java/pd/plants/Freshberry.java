package pd.plants;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Char;
import pd.items.Generator;

public class Freshberry extends Plant {
	{ image = 7; seedClass = Seed.class; }
	@Override public void activate(Char ch) {
		Dungeon.level.drop(Generator.random(Generator.Category.SEED), pos).sprite.drop();
		Dungeon.level.drop(Generator.random(Generator.Category.BERRY), pos).sprite.drop();
	}
	public static class Seed extends Plant.Seed {
		{ image = SpecificPlaceHolderDict.SOMETHING_0; plantClass = Freshberry.class; explantClass = ExFreshberry.class; }
	}
	public static class ExFreshberry extends SpsFruitBush {
		{ image = 7; harvestCount = 3; harvestCategory = Generator.Category.SPS_BERRY; }
	}
}
