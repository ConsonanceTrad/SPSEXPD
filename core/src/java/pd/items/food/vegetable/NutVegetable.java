package pd.items.food.vegetable;

import pd.actors.buffs.Barrier;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.sprites.ItemSpriteSheet;

public class NutVegetable extends Vegetable {
	{ image = ItemSpriteSheet.NUT_VEGETABLE; }
	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, Barrier.class).incShield(Math.max(1, hero.HT / 5));
	}
}
