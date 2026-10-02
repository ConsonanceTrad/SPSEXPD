package pd.items.food.fruit;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.Hunger;
import pd.actors.buffs.Levitation;
import pd.actors.hero.Hero;

public class Strawberry extends Fruit {
	{ image = ConsumFoodFoodDict.STRAWBERRY; energy = Hunger.HUNGRY / 10f; }
	@Override protected void onEat(Hero hero) {
		Buff.prolong(hero, Levitation.class, 20f);
	}
	@Override public int value() { return 5 * quantity; }
}
