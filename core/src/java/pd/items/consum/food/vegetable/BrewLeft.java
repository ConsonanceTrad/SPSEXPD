package pd.items.consum.food.vegetable;

import pd.atlas.items.ConsumPotionSeedBasicPotionDict;

import pd.actors.buffs.Hunger;

public class BrewLeft extends Vegetable {
	{ image = ConsumPotionSeedBasicPotionDict.BREW_LEFT; energy = Hunger.HUNGRY / 10f; hornValue = 0; }
}
