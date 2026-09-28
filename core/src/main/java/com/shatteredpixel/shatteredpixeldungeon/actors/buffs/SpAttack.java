/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

/** Triples attacks against full-health mobs and adds 50% against critically wounded mobs. */
public class SpAttack extends FlavourBuff {
	{ type = buffType.NEUTRAL; announced = true; }
	@Override public int icon() { return BuffIndicator.WEAPON; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}
