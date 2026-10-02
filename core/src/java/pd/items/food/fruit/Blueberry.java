package pd.items.food.fruit;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.Foresight;
import pd.actors.hero.Hero;

public class Blueberry extends Fruit {
	{ image = ConsumFoodFoodDict.BLUEBERRY; }
	@Override protected void onEat(Hero hero) {
		Buff.prolong(hero, Foresight.class, Foresight.DURATION);
	}
}
