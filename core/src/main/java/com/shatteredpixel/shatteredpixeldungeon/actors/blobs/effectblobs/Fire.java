package com.shatteredpixel.shatteredpixeldungeon.actors.blobs.effectblobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

public class Fire extends SpsEffectBlob {
	@Override protected void affect(Char target) { Buff.affect(target, Burning.class).reignite(target, 4f); }
	@Override protected void affect(Heap heap) { heap.burn(); }
	@Override public void use(BlobEmitter emitter) { super.use(emitter); emitter.pour(FlameParticle.FACTORY, 0.03f); }
	@Override public String tileDesc() { return Messages.get(this, "desc"); }
}
