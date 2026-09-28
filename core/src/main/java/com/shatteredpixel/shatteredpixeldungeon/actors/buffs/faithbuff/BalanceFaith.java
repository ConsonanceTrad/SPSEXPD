/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs.faithbuff;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

public class BalanceFaith extends FaithBuff {
	@Override public int icon() { return BuffIndicator.ARMOR; }
	@Override public String desc() { return Messages.get(this, "desc"); }
}
