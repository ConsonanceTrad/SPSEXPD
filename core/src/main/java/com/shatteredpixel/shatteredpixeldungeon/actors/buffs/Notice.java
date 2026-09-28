/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

/** Makes unintentional searches discover every searchable hidden tile in range. */
public class Notice extends FlavourBuff {

	public static final float DURATION = 30f;

	{
		type = buffType.POSITIVE;
		announced = true;
	}

	@Override
	public int icon() {
		return BuffIndicator.FORESIGHT;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", dispTurns());
	}
}
