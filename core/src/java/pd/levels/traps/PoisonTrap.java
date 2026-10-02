package pd.levels.traps;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Poison;
import pd.effects.CellEmitter;
import pd.effects.particles.PoisonParticle;
import pd.items.Heap;
import render.noosa.Game;
import pd.messages.InlineText;

public class PoisonTrap extends Trap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(PoisonTrap.class)
			.t("name", "毒素陷阱")
			.t("desc", "触发这个陷阱会使站在上面的生物中毒。");
	}

	{ color = VIOLET; shape = DIAMOND; }
	@Override public void activate() {
		Char target = Actor.findChar(pos);
		if (target != null) Buff.affect(target, Poison.class).set(4 + Dungeon.legacyDepth() / 2);
		if (Game.instance != null && Game.scene() != null) CellEmitter.center(pos).burst(PoisonParticle.SPLASH, 3);
		Heap heap = Dungeon.level.heaps.get(pos);
		if (heap != null) heap.earthhit();
	}
}
