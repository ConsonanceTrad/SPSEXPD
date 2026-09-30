/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import com.watabou.utils.Bundle;

/** Permanent stacking 10% damage bonus purchased with maximum HP. */
public class Blasphemy extends Buff {
	private static final String LEVEL = "level";
	private int level;
	{ type = buffType.POSITIVE; announced = true; }
	public int level() { return level; }
	public Blasphemy level(int value) { level = Math.max(0, level + value); return this; }
	@Override public boolean act() { spend(TICK); return true; }
	@Override public int icon() { return BuffIndicator.SACRIFICE; }
	@Override public String iconTextDisplay() { return Integer.toString(level); }
	@Override public String desc() { return Messages.get(this, "desc", level); }
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(LEVEL, level); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); level = Math.max(0, bundle.getInt(LEVEL)); }
}
