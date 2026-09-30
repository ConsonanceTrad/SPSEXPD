/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.blobs.damageblobs;

import pd.actors.blobs.SpsElementalDamage;
import pd.actors.damagetype.DamageType;
import pd.effects.BlobEmitter;
import pd.effects.particles.AcidPoolParticle;
import pd.items.Heap;

public class EarthEffectDamage extends SpsElementalDamage {
	@Override protected Object damageSource() { return DamageType.EARTH_DAMAGE; }
	@Override protected void affectHeap(Heap heap) { heap.earthhit(); }
	@Override public void use(BlobEmitter emitter) {
		super.use(emitter);
		emitter.pour(AcidPoolParticle.FACTORY, 0.1f);
	}
}
