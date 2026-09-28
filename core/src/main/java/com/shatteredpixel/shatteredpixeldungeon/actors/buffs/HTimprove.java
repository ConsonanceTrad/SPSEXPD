/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

/** Temporarily raises the hero's unmodified maximum HP by 20%. */
public class HTimprove extends FlavourBuff {
	{ type = buffType.NEUTRAL; announced = true; }
	@Override public boolean attachTo(Char target) {
		if (!super.attachTo(target)) return false;
		if (target instanceof Hero) ((Hero) target).updateHT(true);
		return true;
	}
	@Override public void detach() {
		Char oldTarget = target;
		super.detach();
		if (oldTarget instanceof Hero) ((Hero) oldTarget).updateHT(false);
	}
	@Override public int icon() { return BuffIndicator.BLESS; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}
