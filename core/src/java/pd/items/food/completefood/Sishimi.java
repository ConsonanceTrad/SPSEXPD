/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.food.completefood;

import pd.actors.buffs.Buff;
import pd.actors.buffs.MagicArmor;
import pd.actors.hero.Hero;
import pd.sprites.ItemSpriteSheet;

public class Sishimi extends CompleteFood {
	{
		image = ItemSpriteSheet.MEAT;
		energy = 180f;
	}
	@Override protected void doEat(Hero hero) { Buff.affect(hero, MagicArmor.class).level(hero.HT / 5); }
	@Override public int value() { return 3 * quantity; }
}
