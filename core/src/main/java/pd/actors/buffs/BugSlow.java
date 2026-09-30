/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.items.food.BugMeat;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import render.utils.serialize.Bundle;

public class BugSlow extends Buff {

	private static final String DELAY = "slow_delay";
	private int slowDelay;

	{
		type = buffType.NEGATIVE;
		announced = true;
	}

	@Override
	public boolean act() {
		if (!target.isAlive()) {
			detach();
			return true;
		}
		if (++slowDelay > 8) {
			slowDelay = 0;
			if (target instanceof Hero
					&& Dungeon.hero.belongings.getItem(BugMeat.class) == null) {
				detach();
			} else {
				Buff.prolong(target, Slow.class, 2f);
			}
		}
		spend(TICK);
		return true;
	}

	public void dispel() {
		detach();
	}

	@Override public int icon() { return BuffIndicator.VERTIGO; }
	@Override public String desc() { return Messages.get(this, "desc"); }
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(DELAY, slowDelay); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); slowDelay = bundle.getInt(DELAY); }
}
