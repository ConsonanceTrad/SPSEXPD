/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;

/** Grants gold for successful melee attacks, as in SPS-PD 0.9.8. */
public class GoldTouch extends FlavourBuff {

	public static final float DURATION = 30f;

	{
		type = buffType.POSITIVE;
		announced = true;
	}

	@Override
	public int icon() {
		return BuffIndicator.WEAPON;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", dispTurns());
	}
}
