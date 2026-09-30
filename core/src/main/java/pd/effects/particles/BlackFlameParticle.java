/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.effects.particles;

import watabou.noosa.particles.Emitter;
import watabou.noosa.particles.PixelParticle;

public class BlackFlameParticle extends PixelParticle.Shrinking {

	public static final Emitter.Factory FACTORY = new Emitter.Factory() {
		@Override public void emit(Emitter emitter, int index, float x, float y) {
			((BlackFlameParticle) emitter.recycle(BlackFlameParticle.class)).reset(x, y);
		}
		@Override public boolean lightMode() { return true; }
	};

	public BlackFlameParticle() {
		color(0x000033);
		lifespan = 0.6f;
		acc.set(0, -80);
	}

	public void reset(float x, float y) {
		revive();
		this.x = x;
		this.y = y;
		left = lifespan;
		size = 4;
		speed.set(0);
	}

	@Override public void update() {
		super.update();
		float p = left / lifespan;
		am = p > 0.8f ? (1 - p) * 5 : 1;
	}
}
