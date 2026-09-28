package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PoisonParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.watabou.noosa.Game;

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
