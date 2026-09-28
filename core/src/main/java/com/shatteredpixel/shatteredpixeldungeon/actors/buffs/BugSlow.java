/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.food.BugMeat;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;

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
