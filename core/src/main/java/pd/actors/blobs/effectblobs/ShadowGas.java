package pd.actors.blobs.effectblobs;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.ShadowCurse;
import pd.effects.BlobEmitter;
import pd.effects.particles.BlackFlameParticle;
import pd.items.Heap;
import pd.messages.Messages;

public class ShadowGas extends SpsEffectBlob {
	@Override protected void affect(Char target) {
		if (target.buff(ShadowCurse.class) == null) Buff.affect(target, ShadowCurse.class);
	}
	@Override protected void affect(Heap heap) { heap.darkhit(); }
	@Override public void use(BlobEmitter emitter) { super.use(emitter); emitter.start(BlackFlameParticle.FACTORY, 0.1f, 0); }
	@Override public String tileDesc() { return Messages.get(this, "desc"); }
}
