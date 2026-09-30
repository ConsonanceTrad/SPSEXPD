/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;

public class FunnyBuff extends FlavourBuff {

	public static final float DURATION = 30f;

	{
		type = buffType.NEUTRAL;
	}

	@Override public int icon() { return BuffIndicator.BLESS; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}
