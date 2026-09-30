/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.actors.Char;
import pd.messages.Messages;
import pd.ui.BuffIndicator;

/** Timed marker applied by the clockwork scarecrow after it attacks. */
public class HiddenShadow extends FlavourBuff {
	private boolean applied;
	{
		type = buffType.NEGATIVE;
	}

	@Override
	public int icon() {
		return BuffIndicator.AMOK;
	}

	@Override public boolean attachTo(Char target) {
		if (!super.attachTo(target)) return false;
		target.invisible++;
		applied = true;
		return true;
	}

	@Override public void detach() {
		if (applied && target != null) target.invisible = Math.max(0, target.invisible - 1);
		applied = false;
		super.detach();
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", dispTurns());
	}
}
