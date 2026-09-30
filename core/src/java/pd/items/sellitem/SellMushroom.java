/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.sellitem;

import pd.actors.hero.Hero;
import pd.items.medicine.Pill;
import pd.messages.Messages;
import pd.sprites.ItemSpriteSheet;
import pd.utils.GLog;

public class SellMushroom extends Pill {
	{ image = ItemSpriteSheet.MUSHROOM; }
	@Override protected void onUse(Hero hero) { GLog.w(Messages.get(this, "no")); }
	@Override public int value() { return 100 * quantity; }
}
