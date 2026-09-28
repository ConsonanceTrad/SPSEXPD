/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

/** Increases damage from SPS and Shattered magic sources by 50%. */
public class MagicWeak extends FlavourBuff {
	{ type = buffType.NEGATIVE; announced = true; }
	@Override public int icon() { return BuffIndicator.VULNERABLE; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}
