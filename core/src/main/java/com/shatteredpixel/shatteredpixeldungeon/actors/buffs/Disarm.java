/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

/** Prevents the hero from making ordinary attacks. */
public class Disarm extends FlavourBuff {

	public static final float DURATION = 5f;

	{
		type = buffType.NEGATIVE;
		announced = true;
	}

	@Override
	public int icon() {
		return BuffIndicator.CRIPPLE;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", dispTurns());
	}
}
