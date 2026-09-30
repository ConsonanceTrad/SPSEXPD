/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;

/** While active, each hostile mob kill permanently grants one maximum HP. */
public class Feed extends FlavourBuff {
	{ type = buffType.POSITIVE; announced = true; }
	@Override public int icon() { return BuffIndicator.WELL_FED; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}
