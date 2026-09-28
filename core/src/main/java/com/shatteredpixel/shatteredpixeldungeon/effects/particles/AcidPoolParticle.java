/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.ColorMath;
import com.watabou.utils.Random;

public class AcidPoolParticle extends PixelParticle.Shrinking {

	public static final Emitter.Factory FACTORY = new Emitter.Factory() {
		@Override public void emit(Emitter emitter, int index, float x, float y) {
			((AcidPoolParticle) emitter.recycle(AcidPoolParticle.class)).reset(x, y);
		}
	};

	public AcidPoolParticle() {
		color(ColorMath.random(0x009966, 0x00FF33));
		acc.set(0, -40);
	}

	public void reset(float x, float y) {
		revive();
		this.x = x;
		this.y = y;
		left = lifespan = Random.Float(0.6f, 1f);
		size = 5;
		speed.set(Random.Float(-10, 10), Random.Float(-10, 10));
	}
}
