/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;

/** SPS-PD's two-times movement-speed effect. */
public class HasteBuff extends FlavourBuff {

	public static final float DURATION = 10f;

	{
		type = buffType.POSITIVE;
		announced = true;
	}

	@Override
	public int icon() {
		return BuffIndicator.HASTE;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", dispTurns());
	}
}
