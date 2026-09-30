package pd.items.food.fruit;

import pd.actors.buffs.Buff;
import pd.actors.buffs.Foresight;
import pd.actors.hero.Hero;
import pd.sprites.ItemSpriteSheet;

public class Blueberry extends Fruit {
	{ image = ItemSpriteSheet.BLUEBERRY; }
	@Override protected void onEat(Hero hero) {
		Buff.prolong(hero, Foresight.class, Foresight.DURATION);
	}
}
