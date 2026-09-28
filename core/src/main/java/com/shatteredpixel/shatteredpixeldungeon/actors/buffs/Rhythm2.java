/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

/** Superstar rhythm: +20% speed and damage, and 10% incoming damage reduction. */
public class Rhythm2 extends FlavourBuff {
	public static final float DURATION = 10f;
	{ type = buffType.POSITIVE; announced = true; }
	@Override public int icon() { return BuffIndicator.COMBO; }
}
