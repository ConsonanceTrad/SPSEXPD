/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.effects.particles;

import watabou.noosa.particles.Emitter;
import watabou.noosa.particles.PixelParticle;

/** Green rising particles used by the SPS memory fire. */
public class MemoryParticle extends PixelParticle.Shrinking {

	public static final Emitter.Factory FACTORY = new Emitter.Factory() {
		@Override
		public void emit(Emitter emitter, int index, float x, float y) {
			((MemoryParticle)emitter.recycle(MemoryParticle.class)).reset(x, y);
		}

		@Override public boolean lightMode() { return true; }
	};

	public MemoryParticle() {
		color(0x22CC44);
		lifespan = 0.6f;
		acc.set(0, -100);
	}

	public void reset(float x, float y) {
		revive();
		this.x = x;
		this.y = y - 4;
		left = lifespan;
		size = 4;
		speed.set(0);
	}

	@Override
	public void update() {
		super.update();
		float progress = left / lifespan;
		am = progress > 0.75f ? (1 - progress) * 4 : 1;
	}
}
