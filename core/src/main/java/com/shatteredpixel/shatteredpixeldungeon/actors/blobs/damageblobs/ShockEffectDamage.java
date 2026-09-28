/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.blobs.damageblobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SpsElementalDamage;
import com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EnergyParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;

public class ShockEffectDamage extends SpsElementalDamage {
	@Override protected Object damageSource() { return DamageType.SHOCK_DAMAGE; }
	@Override protected void affectHeap(Heap heap) { heap.shockhit(); }
	@Override public void use(BlobEmitter emitter) {
		super.use(emitter);
		emitter.start(EnergyParticle.FACTORY, 0.1f, 0);
	}
}
