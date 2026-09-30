package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;

public class ItemSteal extends FlavourBuff {
	public static final float DURATION = 30f;
	{ type = buffType.POSITIVE; announced = true; }
	@Override public int icon() { return BuffIndicator.WEAPON; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}
