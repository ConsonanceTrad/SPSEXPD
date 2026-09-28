/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.blobs.damageblobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SpsElementalDamage;
import com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SnowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;

public class IceEffectDamage extends SpsElementalDamage {
	@Override protected Object damageSource() { return DamageType.ICE_DAMAGE; }
	@Override protected void affectHeap(Heap heap) { heap.icehit(); }
	@Override public void use(BlobEmitter emitter) {
		super.use(emitter);
		emitter.start(SnowParticle.FACTORY, 0.1f, 0);
	}
}
