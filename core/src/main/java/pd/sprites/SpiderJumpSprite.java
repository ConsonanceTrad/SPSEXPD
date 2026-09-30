package pd.sprites;

import watabou.noosa.TextureFilm;

public class SpiderJumpSprite extends SpiderNormalSprite {
	public SpiderJumpSprite() {
		super(16);
		TextureFilm frames = new TextureFilm(texture, 16, 16);
		die = new Animation(12, false); die.frames(frames, 26, 27, 27, 27);
	}
}
