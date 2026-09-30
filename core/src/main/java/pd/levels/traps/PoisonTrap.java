package pd.levels.traps;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Poison;
import pd.effects.CellEmitter;
import pd.effects.particles.PoisonParticle;
import pd.items.Heap;
import watabou.noosa.Game;

public class PoisonTrap extends Trap {
	{ color = VIOLET; shape = DIAMOND; }
	@Override public void activate() {
		Char target = Actor.findChar(pos);
		if (target != null) Buff.affect(target, Poison.class).set(4 + Dungeon.legacyDepth() / 2);
		if (Game.instance != null && Game.scene() != null) CellEmitter.center(pos).burst(PoisonParticle.SPLASH, 3);
		Heap heap = Dungeon.level.heaps.get(pos);
		if (heap != null) heap.earthhit();
	}
}
