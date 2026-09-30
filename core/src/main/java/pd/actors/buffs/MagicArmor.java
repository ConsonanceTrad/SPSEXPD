/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import com.watabou.utils.Bundle;

/** Persistent shield consumed only by magic and status damage. */
public class MagicArmor extends Buff {
	private static final String LEVEL = "level";
	private int level;
	{ type = buffType.POSITIVE; announced = true; }
	public MagicArmor level(int value) { level = Math.max(level, value); return this; }
	public int level() { return level; }
	public int absorb(int damage) {
		int absorbed = Math.min(level, Math.max(0, damage));
		level -= absorbed;
		if (level <= 0) detach();
		return damage - absorbed;
	}
	@Override public boolean act() { spend(TICK); return true; }
	@Override public int icon() { return BuffIndicator.ARMOR; }
	@Override public String iconTextDisplay() { return Integer.toString(level); }
	@Override public String desc() { return Messages.get(this, "desc", level); }
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(LEVEL, level); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); level = Math.max(0, bundle.getInt(LEVEL)); }
}
