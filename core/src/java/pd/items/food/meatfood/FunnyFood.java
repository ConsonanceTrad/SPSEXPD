/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.food.meatfood;

import pd.actors.buffs.Buff;
import pd.actors.buffs.FunnyBuff;
import pd.actors.hero.Hero;
import pd.sprites.ItemSpriteSheet;

public class FunnyFood extends MeatFood {
	{
		image = ItemSpriteSheet.BUG_MEAT;
		energy = 500f;
	}
	@Override protected void doEat(Hero hero) {
		Buff.prolong(hero, FunnyBuff.class, 1600f);
	}
	@Override public int value() { return quantity; }
}
