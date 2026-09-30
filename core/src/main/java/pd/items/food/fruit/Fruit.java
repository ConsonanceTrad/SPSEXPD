package pd.items.food.fruit;

import pd.actors.buffs.Hunger;
import pd.actors.hero.Hero;
import pd.items.food.Food;
import pd.sprites.ItemSpriteSheet;

public class Fruit extends Food {
	{
		image = ItemSpriteSheet.BERRY;
		energy = Hunger.HUNGRY / 6f;
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
	@Override public int value() { return 10 * quantity; }
}
