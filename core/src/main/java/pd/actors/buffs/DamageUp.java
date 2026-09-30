/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import watabou.utils.Bundle;

/** Legacy SPS flat damage bonus, consumed by the next successful attack. */
public class DamageUp extends Buff {

	private static final String LEVEL = "level";
	private int level;

	{
		type = buffType.POSITIVE;
		announced = true;
	}

	public DamageUp level(int value) {
		level = Math.max(level, value);
		return this;
	}

	public int level() {
		return level;
	}

	@Override public boolean act() { spend(TICK); return true; }
	@Override public int icon() { return BuffIndicator.FURY; }
	@Override public String desc() { return Messages.get(this, "desc", level); }

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(LEVEL, level);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		level = Math.max(0, bundle.getInt(LEVEL));
	}
}
