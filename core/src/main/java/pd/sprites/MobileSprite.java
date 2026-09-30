package pd.sprites;

import pd.Assets;
import render.noosa.TextureFilm;

public class MobileSprite extends MobSprite {
	public MobileSprite() {
		this(0);
	}

	protected MobileSprite(int offset) {
		texture(Assets.Sprites.MOBILE);
		TextureFilm frames = new TextureFilm(texture, 16, 16);
		idle = new Animation(3, true);
		idle.frames(frames, offset, offset, offset, offset+1, offset+1, offset+1,
				offset+2, offset+2, offset+2, offset+3, offset+3, offset+3);
		run = new Animation(20, true);
		run.frames(frames, offset);
		attack = new Animation(12, false);
		attack.frames(frames, offset, offset+2, offset+3);
		die = new Animation(20, false);
		die.frames(frames, offset);
		play(idle);
	}
}
