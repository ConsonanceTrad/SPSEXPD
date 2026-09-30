/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.actors.Char;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import watabou.utils.Bundle;

/** Damages and roots the target if it moves before this charge expires. */
public class Shocked2 extends Buff {

	public static final float DURATION = 5f;
	private static final String START_POS = "start_pos";
	private static final String LEFT = "left";

	private int startPos;
	private float left;

	{
		type = buffType.NEGATIVE;
		announced = true;
	}

	@Override
	public boolean attachTo(Char target) {
		if (!super.attachTo(target)) return false;
		startPos = target.pos;
		left = Math.max(left, DURATION);
		return true;
	}

	public Shocked2 level(int value) {
		left = Math.max(left, value);
		return this;
	}

	public float level() { return left; }

	@Override
	public boolean act() {
		if (!target.isAlive()) {
			detach();
			return true;
		}
		if (target.pos != startPos) {
			target.damage(Math.min(1000, target.HT / 20), this);
			if (target.isAlive()) Buff.prolong(target, Roots.class, Roots.DURATION);
			detach();
			return true;
		}
		left -= TICK;
		spend(TICK);
		if (left <= 0) detach();
		return true;
	}

	@Override public int icon() { return BuffIndicator.RECHARGING; }
	@Override public String iconTextDisplay() { return Integer.toString(Math.max(0, (int)Math.ceil(left))); }
	@Override public String desc() { return Messages.get(this, "desc", Math.max(0, (int)Math.ceil(left))); }

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(START_POS, startPos);
		bundle.put(LEFT, left);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		startPos = bundle.getInt(START_POS);
		left = Math.max(0, bundle.getFloat(LEFT));
	}
}
