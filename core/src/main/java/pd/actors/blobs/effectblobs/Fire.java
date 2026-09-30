package pd.actors.blobs.effectblobs;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.effects.BlobEmitter;
import pd.effects.particles.FlameParticle;
import pd.items.Heap;
import pd.messages.Messages;

public class Fire extends SpsEffectBlob {
	@Override protected void affect(Char target) { Buff.affect(target, Burning.class).reignite(target, 4f); }
	@Override protected void affect(Heap heap) { heap.burn(); }
	@Override public void use(BlobEmitter emitter) { super.use(emitter); emitter.pour(FlameParticle.FACTORY, 0.03f); }
	@Override public String tileDesc() { return Messages.get(this, "desc"); }
}
