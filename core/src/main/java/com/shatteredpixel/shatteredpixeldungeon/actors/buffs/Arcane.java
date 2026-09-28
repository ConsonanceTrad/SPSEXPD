/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

/** Marker used by SPS effects which temporarily amplify magic. */
public class Arcane extends FlavourBuff {
	public static final float DURATION = 30f;
	{ type = buffType.POSITIVE; announced = true; }
	@Override public int icon() { return BuffIndicator.IMMUNITY; }
}
