package pd.actors.blobs.effectblobs;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.LightShootAttack;
import pd.effects.BlobEmitter;
import pd.effects.particles.ShaftParticle;
import pd.items.Heap;
import pd.messages.Messages;

public class HolyLight extends SpsEffectBlob {
	@Override protected void affect(Char target) { Buff.affect(target, LightShootAttack.class).level(5); }
	@Override protected void affect(Heap heap) { heap.lighthit(); }
	@Override public void use(BlobEmitter emitter) { super.use(emitter); emitter.start(ShaftParticle.FACTORY, 1f, 0); }
	@Override public String tileDesc() { return Messages.get(this, "desc"); }
}
