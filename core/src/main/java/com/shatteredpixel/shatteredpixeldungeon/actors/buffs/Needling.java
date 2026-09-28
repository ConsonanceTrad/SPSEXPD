/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Random;

public class Needling extends FlavourBuff {
	{ type = buffType.POSITIVE; announced = true; }
	public void proc(Char enemy) {
		if (Random.Int(2) == 0) Buff.prolong(enemy, ArmorBreak.class, 5f).level(50);
		else Buff.affect(enemy, Bleeding.class).set(10f);
	}
	@Override public int icon() { return BuffIndicator.THORNS; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}
