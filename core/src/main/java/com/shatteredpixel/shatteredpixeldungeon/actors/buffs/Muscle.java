/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

/** Legacy SPS strength infusion. The actual +2 STR is applied by Hero.STR(). */
public class Muscle extends FlavourBuff {
	{ type = buffType.POSITIVE; announced = true; }
	@Override public int icon() { return BuffIndicator.FURY; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}
