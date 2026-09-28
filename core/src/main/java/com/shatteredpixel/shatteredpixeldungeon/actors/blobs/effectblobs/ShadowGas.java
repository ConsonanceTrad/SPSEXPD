package com.shatteredpixel.shatteredpixeldungeon.actors.blobs.effectblobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShadowCurse;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlackFlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

public class ShadowGas extends SpsEffectBlob {
	@Override protected void affect(Char target) {
		if (target.buff(ShadowCurse.class) == null) Buff.affect(target, ShadowCurse.class);
	}
	@Override protected void affect(Heap heap) { heap.darkhit(); }
	@Override public void use(BlobEmitter emitter) { super.use(emitter); emitter.start(BlackFlameParticle.FACTORY, 0.1f, 0); }
	@Override public String tileDesc() { return Messages.get(this, "desc"); }
}
