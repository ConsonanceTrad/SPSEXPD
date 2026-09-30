package pd.sprites;

import pd.Assets;
import render.noosa.TextureFilm;

public class FairySprite extends MobSprite {
	public FairySprite() {
		this(0);
	}

	protected FairySprite(int offset) {
		texture(Assets.Sprites.FAIRY);
		TextureFilm frames = new TextureFilm(texture, 15, 15);
		idle = new Animation(2, true);
		idle.frames(frames, offset, offset+2, offset+3, offset);
		run = new Animation(8, true);
		run.frames(frames, offset, offset+1, offset+2, offset);
		attack = new Animation(15, false);
		attack.frames(frames, offset, offset+3, offset+4, offset+1);
		die = new Animation(8, false);
		die.frames(frames, offset+5, offset+6, offset+7, offset+7);
		play(idle);
	}

	@Override
	public int blood() {
		return 0xFFcdcdb7;
	}
}
