/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;

/** Temporarily bypasses the class-skill cooldown. */
public class SkillRecharge extends FlavourBuff {

	public static final float DURATION = 40f;

	{
		type = buffType.POSITIVE;
		announced = true;
	}

	@Override
	public int icon() {
		return BuffIndicator.RECHARGING;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", dispTurns());
	}
}
