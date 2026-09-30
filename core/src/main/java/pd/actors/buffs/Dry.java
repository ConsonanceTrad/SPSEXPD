/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;

public class Dry extends FlavourBuff {
	public static final float DURATION = 10f;
	{ type = buffType.NEGATIVE; announced = true; }
	@Override public int icon() { return BuffIndicator.OOZE; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}
