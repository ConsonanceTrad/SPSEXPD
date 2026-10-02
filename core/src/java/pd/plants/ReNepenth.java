package pd.plants;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Char;
import pd.items.Generator;
import pd.items.TransmutationBall;

public class ReNepenth extends Plant {
	{ image = 14; seedClass = Seed.class; }
	@Override public void activate(Char ch) {
		Dungeon.level.drop(new TransmutationBall(), pos).sprite.drop();
		Dungeon.level.drop(Generator.random(Generator.Category.SPS_BERRY), pos).sprite.drop();
	}
	public static class Seed extends Plant.Seed {
		{ image = SpecificPlaceHolderDict.SOMETHING_0; plantClass = ReNepenth.class; explantClass = ExReNepenth.class; }
	}
	public static class ExReNepenth extends SpsFruitBush {
		{ image = 14; harvestCount = 2; harvestClass = TransmutationBall.class; }
	}
}
