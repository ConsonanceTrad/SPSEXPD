package pd.levels.traps;

import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.messages.Messages;
import pd.utils.GLog;
import render.noosa.Game;
import pd.messages.InlineText;

public class WornTrap extends Trap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(WornTrap.class)
			.t("name", "破旧的陷阱")
			.t("nothing", "真的什么都没发生……")
			.t("desc", "这个古老的陷阱已经彻底失效，触发时什么都不会发生。");
	}



	{ color = BLACK; shape = DOTS; canBeHidden = false; }
	@Override public void activate() {
		if (Game.instance != null && Game.scene() != null) CellEmitter.get(pos).burst(Speck.factory(Speck.STEAM), 6);
		GLog.i(Messages.get(this, "nothing"));
	}
}
