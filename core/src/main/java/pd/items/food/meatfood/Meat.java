/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.food.meatfood;

import pd.actors.buffs.Buff;
import pd.actors.buffs.Poison;
import pd.actors.hero.Hero;
import pd.sprites.ItemSpriteSheet;
import watabou.utils.Random;

public class Meat extends MeatFood {
	{
		image = ItemSpriteSheet.MEAT;
		energy = 100f;
	}
	@Override protected void doEat(Hero hero) {
		if (Random.Int(15) == 0) Buff.affect(hero, Poison.class).set(hero.HT / 5f);
	}
	@Override public int value() { return 2 * quantity; }
}
