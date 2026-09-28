/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs.mindbuff;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

/** Persistent positive mental state: each future level grants one extra maximum HP. */
public class HopeMind extends Buff {
	{ type = buffType.POSITIVE; announced = true; }
	@Override public boolean act() { if (target != null && target.isAlive()) spend(TICK); return true; }
	@Override public int icon() { return BuffIndicator.MIND_VISION; }
	@Override public String desc() { return Messages.get(this, "desc"); }
}
