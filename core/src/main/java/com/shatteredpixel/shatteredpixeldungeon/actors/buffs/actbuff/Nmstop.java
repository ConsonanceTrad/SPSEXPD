/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs.actbuff;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

public class Nmstop extends FlavourBuff {
	public static final float DURATION = 10f;
	{ type = buffType.NEUTRAL; }
	@Override public int icon() { return BuffIndicator.FROST; }
}
