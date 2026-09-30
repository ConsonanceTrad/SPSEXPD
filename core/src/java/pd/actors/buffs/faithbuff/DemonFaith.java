/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs.faithbuff;

import pd.messages.Messages;
import pd.ui.BuffIndicator;

public class DemonFaith extends FaithBuff {
	@Override public int icon() { return BuffIndicator.CORRUPT; }
	@Override public String desc() { return Messages.get(this, "desc"); }
}
