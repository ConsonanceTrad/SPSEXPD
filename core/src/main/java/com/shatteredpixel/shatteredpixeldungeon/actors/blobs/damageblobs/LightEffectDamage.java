/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.blobs.damageblobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SpsElementalDamage;
import com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShaftParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;

public class LightEffectDamage extends SpsElementalDamage {
	@Override protected Object damageSource() { return DamageType.LIGHT_DAMAGE; }
	@Override protected void affectHeap(Heap heap) { heap.lighthit(); }
	@Override public void use(BlobEmitter emitter) {
		super.use(emitter);
		emitter.start(ShaftParticle.FACTORY, 1f, 0);
	}
}
