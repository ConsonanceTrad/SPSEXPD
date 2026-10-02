/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.specific.sellitem;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.hero.Hero;
import pd.items.consum.medicine.Pill;
import pd.messages.Messages;
import pd.utils.GLog;

public class SellMushroom extends Pill {
	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override protected void onUse(Hero hero) { GLog.w(Messages.get(this, "no")); }
	@Override public int value() { return 100 * quantity; }
}
