/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs.mindbuff;

import pd.actors.buffs.Buff;
import pd.messages.Messages;
import pd.ui.BuffIndicator;

/** Persistent positive mental state: each future level grants one extra maximum HP. */
public class HopeMind extends Buff {
	{ type = buffType.POSITIVE; announced = true; }
	@Override public boolean act() { if (target != null && target.isAlive()) spend(TICK); return true; }
	@Override public int icon() { return BuffIndicator.MIND_VISION; }
	@Override public String desc() { return Messages.get(this, "desc"); }
}
