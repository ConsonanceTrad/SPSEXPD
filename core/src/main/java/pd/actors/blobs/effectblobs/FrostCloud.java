package pd.actors.blobs.effectblobs;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.FrostIce;
import pd.effects.BlobEmitter;
import pd.effects.particles.SnowParticle;
import pd.items.Heap;
import pd.messages.Messages;

public class FrostCloud extends SpsEffectBlob {
	@Override protected void affect(Char target) { Buff.affect(target, FrostIce.class).level(10); }
	@Override protected void affect(Heap heap) { heap.freeze(); }
	@Override public void use(BlobEmitter emitter) { super.use(emitter); emitter.start(SnowParticle.FACTORY, 0.05f, 0); }
	@Override public String tileDesc() { return Messages.get(this, "desc"); }
}
