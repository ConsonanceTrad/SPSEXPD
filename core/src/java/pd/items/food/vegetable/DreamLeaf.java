package pd.items.food.vegetable;

import pd.atlas.items.ConsumPotionSeedSeedDict;

import pd.actors.hero.Hero;
import pd.items.potions.PotionOfHealing;

public class DreamLeaf extends Vegetable {
	{ image = ConsumPotionSeedSeedDict.DREAM_LEAF; }
	@Override protected void onEat(Hero hero) {
		PotionOfHealing.cure(hero);
	}
}
