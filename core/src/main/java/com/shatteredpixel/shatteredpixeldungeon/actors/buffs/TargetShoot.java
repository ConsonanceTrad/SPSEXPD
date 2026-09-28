/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

/** Legacy aimed-shot state. Missile damage is multiplied in Hero.damageRoll(). */
public class TargetShoot extends FlavourBuff {
	{ type = buffType.POSITIVE; announced = true; }
	@Override public int icon() { return BuffIndicator.MARK; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}
