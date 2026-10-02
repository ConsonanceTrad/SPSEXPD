package pd.items.consum.food.vegetable;

import pd.atlas.items.ConsumPotionSeedSeedDict;

import pd.actors.hero.Hero;
import pd.items.consum.potions.PotionOfHealing;

public class DreamLeaf extends Vegetable {
	{ image = ConsumPotionSeedSeedDict.DREAM_LEAF; }
	@Override protected void onEat(Hero hero) {
		PotionOfHealing.cure(hero);
	}
}
