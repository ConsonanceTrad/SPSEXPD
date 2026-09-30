package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;

public class DeadRaise extends FlavourBuff {
	public static final float DURATION = 5f;
	{ type = buffType.NEGATIVE; announced = true; }
	@Override public int icon() { return BuffIndicator.TERROR; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}
