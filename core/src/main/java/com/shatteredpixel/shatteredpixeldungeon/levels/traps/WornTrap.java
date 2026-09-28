package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;

public class WornTrap extends Trap {
	{ color = BLACK; shape = DOTS; canBeHidden = false; }
	@Override public void activate() {
		if (Game.instance != null && Game.scene() != null) CellEmitter.get(pos).burst(Speck.factory(Speck.STEAM), 6);
		GLog.i(Messages.get(this, "nothing"));
	}
}
