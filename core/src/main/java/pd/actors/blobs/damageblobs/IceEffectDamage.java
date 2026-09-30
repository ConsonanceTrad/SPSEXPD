/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.blobs.damageblobs;

import pd.actors.blobs.SpsElementalDamage;
import pd.actors.damagetype.DamageType;
import pd.effects.BlobEmitter;
import pd.effects.particles.SnowParticle;
import pd.items.Heap;

public class IceEffectDamage extends SpsElementalDamage {
	@Override protected Object damageSource() { return DamageType.ICE_DAMAGE; }
	@Override protected void affectHeap(Heap heap) { heap.icehit(); }
	@Override public void use(BlobEmitter emitter) {
		super.use(emitter);
		emitter.start(SnowParticle.FACTORY, 0.1f, 0);
	}
}
