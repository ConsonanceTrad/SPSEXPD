/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.blobs.damageblobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SpsElementalDamage;
import com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlackFlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;

public class DarkEffectDamage extends SpsElementalDamage {
	@Override protected Object damageSource() { return DamageType.DARK_DAMAGE; }
	@Override protected void affectHeap(Heap heap) { heap.darkhit(); }
	@Override public void use(BlobEmitter emitter) {
		super.use(emitter);
		emitter.start(BlackFlameParticle.FACTORY, 0.03f, 0);
	}
}
