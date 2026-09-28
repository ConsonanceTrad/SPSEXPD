package com.shatteredpixel.shatteredpixeldungeon.actors.blobs.effectblobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.AcidPoolParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

public class AcidWater extends SpsEffectBlob {
	@Override protected void affect(Char target) { Buff.affect(target, Ooze.class).set(3f); }
	@Override protected void affect(Heap heap) { heap.earthhit(); }
	@Override public void use(BlobEmitter emitter) { super.use(emitter); emitter.pour(AcidPoolParticle.FACTORY, 0.1f); }
	@Override public String tileDesc() { return Messages.get(this, "desc"); }
}
