package pd.items.consum.food.vegetable;

import pd.atlas.items.ConsumPotionSeedSeedDict;

import pd.actors.buffs.Barrier;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;

public class NutVegetable extends Vegetable {
	{ image = ConsumPotionSeedSeedDict.NUT_VEGETABLE; }
	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, Barrier.class).incShield(Math.max(1, hero.HT / 5));
	}
}
