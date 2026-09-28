package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

public class DeadRaise extends FlavourBuff {
	public static final float DURATION = 5f;
	{ type = buffType.NEGATIVE; announced = true; }
	@Override public int icon() { return BuffIndicator.TERROR; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}
