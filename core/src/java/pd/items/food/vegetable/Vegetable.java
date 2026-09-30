package pd.items.food.vegetable;

import pd.actors.buffs.Hunger;
import pd.actors.hero.Hero;
import pd.items.food.Food;
import pd.sprites.ItemSpriteSheet;

public class Vegetable extends Food {
	{
		image = ItemSpriteSheet.RATION;
		energy = Hunger.HUNGRY / 15f;
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
