/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

/** Triples the hero's next successful attack damage roll. */
public class MoonFury extends Buff {

	{
		type = buffType.POSITIVE;
		announced = true;
	}

	@Override
	public int icon() {
		return BuffIndicator.FURY;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc");
	}

	@Override
	public void detach() {
		FullMoonStrength strength = target == null ? null : target.buff(FullMoonStrength.class);
		if (strength != null) {
			strength.detach();
		} else {
			super.detach();
		}
	}
}
