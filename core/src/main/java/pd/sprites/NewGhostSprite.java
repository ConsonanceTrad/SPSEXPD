/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.Assets;
import pd.effects.Speck;
import pd.effects.particles.ShaftParticle;
import com.watabou.glwrap.Blending;
import com.watabou.noosa.TextureFilm;

/** Original SPS-PD 0.9.8 dried-rose ghost. */
public class NewGhostSprite extends MobSprite {
	public NewGhostSprite() {
		texture(Assets.Sprites.SPS_NEW_GHOST);
		TextureFilm frames = new TextureFilm(texture, 14, 15);
		idle = new Animation(5, true); idle.frames(frames, 0, 1);
		run = new Animation(10, true); run.frames(frames, 0, 1);
		attack = new Animation(10, false); attack.frames(frames, 0, 2, 3);
		die = new Animation(8, false); die.frames(frames, 0, 4, 5, 6, 7);
		play(idle);
	}
	@Override public void draw() { Blending.setLightMode(); super.draw(); Blending.setNormalMode(); }
	@Override public void die() { super.die(); emitter().start(ShaftParticle.FACTORY, 0.3f, 4); emitter().start(Speck.factory(Speck.LIGHT), 0.2f, 3); }
	@Override public int blood() { return 0xFFFFFF; }
}
