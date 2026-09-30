package pd.effects.particles;

import render.noosa.particles.Emitter;
import render.noosa.particles.PixelParticle;
import render.utils.Random;

public class DeadParticle extends PixelParticle {
	public static final Emitter.Factory FACTORY = new Emitter.Factory() {
		@Override public void emit(Emitter emitter, int index, float x, float y) {
			((DeadParticle) emitter.recycle(DeadParticle.class)).reset(x, y);
		}
		@Override public boolean lightMode() { return true; }
	};
	public DeadParticle() { color(0x222222); lifespan = 1f; speed.set(0, 8); }
	public void reset(float x, float y) { revive(); this.x = x; this.y = y; left = lifespan + Random.Float(lifespan); }
	@Override public void update() { super.update(); float p = left / lifespan; am = p < 0.5f ? p : 1 - p; scale.x = (1 - p) * 2; scale.y = 16 + (1 - p) * 16; }
}
