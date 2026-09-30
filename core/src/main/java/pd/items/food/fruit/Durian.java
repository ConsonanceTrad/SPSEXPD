package pd.items.food.fruit;

import pd.actors.buffs.Barkskin;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Hunger;
import pd.actors.hero.Hero;
import pd.sprites.ItemSpriteSheet;

public class Durian extends Fruit {
	{ image = ItemSpriteSheet.DURIAN; energy = Hunger.HUNGRY / 3f; }
	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, Barkskin.class).set(4 + hero.lvl / 3, 30);
	}
	@Override public int value() { return 5 * quantity; }
}
