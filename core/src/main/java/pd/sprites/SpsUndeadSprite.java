package pd.sprites;

import pd.Assets;
import pd.effects.Speck;
import watabou.noosa.TextureFilm;

public class SpsUndeadSprite extends MobSprite {
	public SpsUndeadSprite() {
		texture(Assets.Sprites.SPS_UNDEAD);
		TextureFilm frames = new TextureFilm(texture, 12, 16);
		idle = new Animation(12, true); idle.frames(frames, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3);
		run = new Animation(15, true); run.frames(frames, 4, 5, 6, 7, 8, 9);
		attack = new Animation(15, false); attack.frames(frames, 14, 15, 16);
		die = new Animation(12, false); die.frames(frames, 10, 11, 12, 13);
		play(idle);
	}
	@Override public void die() { emitter().burst(Speck.factory(Speck.BONE), 3); super.die(); }
	@Override public int blood() { return 0xFFcccccc; }
}
