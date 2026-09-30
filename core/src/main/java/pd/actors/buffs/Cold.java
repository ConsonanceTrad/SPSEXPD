package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;

public class Cold extends FlavourBuff {
	public static final float DURATION = 10f;
	{ type = buffType.NEGATIVE; announced = true; }
	@Override public int icon() { return BuffIndicator.FROST; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}
