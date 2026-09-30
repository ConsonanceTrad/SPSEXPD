package pd.sprites;

import pd.Assets;
import watabou.noosa.TextureFilm;

public class ScorpionSprite extends MobSprite {
	public ScorpionSprite() {
		texture(Assets.Sprites.SCORPIO);
		TextureFilm frames = new TextureFilm(texture, 18, 17);
		idle = new Animation(12, true); idle.frames(frames, 28, 28, 28, 28, 28, 28, 28, 28, 29, 30, 29, 30, 29, 30);
		run = new Animation(8, true); run.frames(frames, 33, 33, 34, 34);
		attack = new Animation(15, false); attack.frames(frames, 28, 31, 32);
		die = new Animation(12, false); die.frames(frames, 28, 35, 36, 37, 38);
		play(idle);
	}
	@Override public int blood() { return 0xFF44FF22; }
}
