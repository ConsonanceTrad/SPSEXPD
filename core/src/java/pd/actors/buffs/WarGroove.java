/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;

/** The next successful physical attack deals 50% more damage. */
public class WarGroove extends Buff {
	{
		type = buffType.POSITIVE;
		announced = true;
	}
	@Override public int icon() { return BuffIndicator.WEAPON; }
	@Override public String desc() { return Messages.get(this, "desc"); }
}
