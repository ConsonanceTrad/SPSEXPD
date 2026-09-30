/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.effects.particles;

import pd.Assets;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Random;

public class ShockWebParticle extends Image {

	private static final int SIZE = 7;
	private static final int COBWEB_FRAME = 15;
	private static TextureFilm film;

	public static final Emitter.Factory FACTORY = new Emitter.Factory() {
		@Override public void emit(Emitter emitter, int index, float x, float y) {
			((ShockWebParticle) emitter.recycle(ShockWebParticle.class)).reset(x, y);
		}
	};

	private float lifespan;
	private float left;

	public ShockWebParticle() {
		texture(Assets.Effects.SPS_SPECKS);
		if (film == null) film = new TextureFilm(texture, SIZE, SIZE);
		frame(film.get(COBWEB_FRAME));
		origin.set(SIZE / 2f);
	}

	public void reset(float x, float y) {
		revive();
		this.x = x;
		this.y = y;
		angle = Random.Float(360);
		left = lifespan = Random.Float(1f, 3f);
		resetColor();
		scale.set(1);
	}

	@Override public void update() {
		super.update();
		left -= Game.elapsed;
		if (left <= 0) {
			kill();
			return;
		}
		float p = 1 - left / lifespan;
		am = p < 0.5f ? p : 1 - p;
		scale.set(1 + p);
	}
}
