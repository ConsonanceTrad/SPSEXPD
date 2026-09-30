/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.blobs.damageblobs;

import pd.actors.blobs.SpsElementalDamage;
import pd.actors.damagetype.DamageType;
import pd.effects.BlobEmitter;
import pd.effects.particles.ShaftParticle;
import pd.items.Heap;

public class LightEffectDamage extends SpsElementalDamage {
	@Override protected Object damageSource() { return DamageType.LIGHT_DAMAGE; }
	@Override protected void affectHeap(Heap heap) { heap.lighthit(); }
	@Override public void use(BlobEmitter emitter) {
		super.use(emitter);
		emitter.start(ShaftParticle.FACTORY, 1f, 0);
	}
}
