/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

/** Doubles movement speed, raises outgoing damage by 50%, and incoming damage by 10%. */
public class SpeedImbue extends FlavourBuff {
	{ type = buffType.NEUTRAL; announced = true; }
	@Override public int icon() { return BuffIndicator.HASTE; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}
