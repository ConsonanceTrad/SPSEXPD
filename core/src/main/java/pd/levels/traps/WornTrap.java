package pd.levels.traps;

import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.messages.Messages;
import pd.utils.GLog;
import watabou.noosa.Game;

public class WornTrap extends Trap {
	{ color = BLACK; shape = DOTS; canBeHidden = false; }
	@Override public void activate() {
		if (Game.instance != null && Game.scene() != null) CellEmitter.get(pos).burst(Speck.factory(Speck.STEAM), 6);
		GLog.i(Messages.get(this, "nothing"));
	}
}
