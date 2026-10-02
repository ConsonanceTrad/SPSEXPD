package pd.items.consum.food.fruit;

import pd.atlas.items.ConsumPotionSeedSeedDict;

import pd.actors.buffs.Barkskin;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Hunger;
import pd.actors.hero.Hero;

public class Durian extends Fruit {
	{ image = ConsumPotionSeedSeedDict.DURIAN; energy = Hunger.HUNGRY / 3f; }
	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, Barkskin.class).set(4 + hero.lvl / 3, 30);
	}
	@Override public int value() { return 5 * quantity; }
}
