/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

/** Performer state: attacks sometimes gain 20% damage and hits sometimes lose 20% damage. */
public class HighVoice extends FlavourBuff {
	{ type = buffType.NEUTRAL; announced = true; }
	@Override public int icon() { return BuffIndicator.HEART; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}
