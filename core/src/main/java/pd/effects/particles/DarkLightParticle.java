/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.effects.particles;

import watabou.noosa.particles.Emitter;
import watabou.noosa.particles.PixelParticle;
import watabou.utils.Random;

public class DarkLightParticle extends PixelParticle {

	public static final Emitter.Factory FACTORY = new Emitter.Factory() {
		@Override public void emit(Emitter emitter, int index, float x, float y) {
			((DarkLightParticle) emitter.recycle(DarkLightParticle.class)).reset(x, y);
		}
		@Override public boolean lightMode() { return true; }
	};

	public DarkLightParticle() {
		color(0x2F4F4F);
		lifespan = 1f;
		speed.set(0, 8);
	}

	public void reset(float x, float y) {
		revive();
		this.x = x;
		this.y = y;
		float offset = -Random.Float(lifespan);
		left = lifespan - offset;
	}

	@Override public void update() {
		super.update();
		float p = left / lifespan;
		am = p < 0.5f ? p : 1 - p;
		scale.x = (1 - p) * 2;
		scale.y = 16 + (1 - p) * 16;
	}
}
