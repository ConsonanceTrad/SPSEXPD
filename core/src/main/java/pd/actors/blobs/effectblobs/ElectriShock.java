package pd.actors.blobs.effectblobs;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Shocked;
import pd.effects.BlobEmitter;
import pd.effects.particles.EnergyParticle;
import pd.items.Heap;
import pd.messages.Messages;

public class ElectriShock extends SpsEffectBlob {
	@Override protected void affect(Char target) { Buff.affect(target, Shocked.class).set(10f); }
	@Override protected void affect(Heap heap) { heap.shockhit(); }
	@Override public void use(BlobEmitter emitter) { super.use(emitter); emitter.start(EnergyParticle.FACTORY, 0.05f, 0); }
	@Override public String tileDesc() { return Messages.get(this, "desc"); }
}
