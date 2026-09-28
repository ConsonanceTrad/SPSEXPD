package com.shatteredpixel.shatteredpixeldungeon.actors.blobs.effectblobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FrostIce;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SnowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

public class FrostCloud extends SpsEffectBlob {
	@Override protected void affect(Char target) { Buff.affect(target, FrostIce.class).level(10); }
	@Override protected void affect(Heap heap) { heap.freeze(); }
	@Override public void use(BlobEmitter emitter) { super.use(emitter); emitter.start(SnowParticle.FACTORY, 0.05f, 0); }
	@Override public String tileDesc() { return Messages.get(this, "desc"); }
}
