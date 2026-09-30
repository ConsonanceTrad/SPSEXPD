/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;

/** Temporary complete damage immunity granted by the Mushroom Kingdom box. */
public class BoxStar extends FlavourBuff {
	public static final float DURATION = 30f;
	{ type = buffType.POSITIVE; announced = true; }
	@Override public int icon() { return BuffIndicator.IMMUNITY; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}
