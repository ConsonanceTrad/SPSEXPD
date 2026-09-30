/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.blobs.damageblobs;

import pd.actors.blobs.SpsElementalDamage;
import pd.actors.damagetype.DamageType;
import pd.effects.BlobEmitter;
import pd.effects.particles.EnergyParticle;
import pd.items.Heap;

public class ShockEffectDamage extends SpsElementalDamage {
	@Override protected Object damageSource() { return DamageType.SHOCK_DAMAGE; }
	@Override protected void affectHeap(Heap heap) { heap.shockhit(); }
	@Override public void use(BlobEmitter emitter) {
		super.use(emitter);
		emitter.start(EnergyParticle.FACTORY, 0.1f, 0);
	}
}
