package pd.items.consum.food.vegetable;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Hunger;
import pd.actors.hero.Hero;
import pd.items.consum.food.Food;

public class Vegetable extends Food {
	{
		image = SpecificPlaceHolderDict.FOOD_HOLDER_0;
		energy = 20f; //SPSEXPD: 原 Hunger.HUNGRY / 15f（批 3 将调到 50）
		hornValue = 1;
		bones = false;
	}
	@Override protected void satisfy(Hero hero) {
		super.satisfy(hero);
		onEat(hero);
	}
	protected void onEat(Hero hero) {
	}
	@Override protected float eatingTime() { return 1f; }
	@Override public int value() { return quantity; }
}
