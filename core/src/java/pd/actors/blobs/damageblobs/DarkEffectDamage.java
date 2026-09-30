/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.blobs.damageblobs;

import pd.actors.blobs.SpsElementalDamage;
import pd.actors.damagetype.DamageType;
import pd.effects.BlobEmitter;
import pd.effects.particles.BlackFlameParticle;
import pd.items.Heap;

public class DarkEffectDamage extends SpsElementalDamage {
	@Override protected Object damageSource() { return DamageType.DARK_DAMAGE; }
	@Override protected void affectHeap(Heap heap) { heap.darkhit(); }
	@Override public void use(BlobEmitter emitter) {
		super.use(emitter);
		emitter.start(BlackFlameParticle.FACTORY, 0.03f, 0);
	}
}
