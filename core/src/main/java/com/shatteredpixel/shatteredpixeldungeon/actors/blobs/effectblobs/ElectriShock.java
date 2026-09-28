package com.shatteredpixel.shatteredpixeldungeon.actors.blobs.effectblobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Shocked;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EnergyParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

public class ElectriShock extends SpsEffectBlob {
	@Override protected void affect(Char target) { Buff.affect(target, Shocked.class).set(10f); }
	@Override protected void affect(Heap heap) { heap.shockhit(); }
	@Override public void use(BlobEmitter emitter) { super.use(emitter); emitter.start(EnergyParticle.FACTORY, 0.05f, 0); }
	@Override public String tileDesc() { return Messages.get(this, "desc"); }
}
