package pd.effects.particles;

import watabou.noosa.particles.Emitter;
import watabou.noosa.particles.PixelParticle;
import watabou.utils.Random;

public class SandParticle extends PixelParticle {
	public static final Emitter.Factory FACTORY = new Emitter.Factory() {
		@Override public void emit(Emitter emitter, int index, float x, float y) {
			((SandParticle) emitter.recycle(SandParticle.class)).reset(x, y);
		}
	};
	public SandParticle() { color(0xFFCC00); speed.set(0, Random.Float(5, 8)); lifespan = 1.2f; }
	public void reset(float x, float y) { revive(); this.x = x; this.y = y - speed.y * lifespan; left = lifespan; }
	@Override public void update() { super.update(); float p = left / lifespan; am = (p < 0.5f ? p : 1 - p) * 1.5f; }
}
