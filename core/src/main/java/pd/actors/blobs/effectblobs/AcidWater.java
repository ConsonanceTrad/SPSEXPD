package pd.actors.blobs.effectblobs;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Ooze;
import pd.effects.BlobEmitter;
import pd.effects.particles.AcidPoolParticle;
import pd.items.Heap;
import pd.messages.Messages;

public class AcidWater extends SpsEffectBlob {
	@Override protected void affect(Char target) { Buff.affect(target, Ooze.class).set(3f); }
	@Override protected void affect(Heap heap) { heap.earthhit(); }
	@Override public void use(BlobEmitter emitter) { super.use(emitter); emitter.pour(AcidPoolParticle.FACTORY, 0.1f); }
	@Override public String tileDesc() { return Messages.get(this, "desc"); }
}
