package pd.sprites;

import pd.Assets;
import render.noosa.TextureFilm;

public class BlueGirlSprite extends MobSprite {
	public BlueGirlSprite() {
		texture(Assets.Sprites.SPS_BLUE_GIRL);
		TextureFilm frames = new TextureFilm(texture, 16, 16);
		idle = new Animation(12, true); idle.frames(frames, 0, 1, 2);
		run = new Animation(15, true); run.frames(frames, 6, 7, 8, 9, 10, 11, 1);
		attack = new Animation(15, false); attack.frames(frames, 2, 3, 4, 3, 4);
		die = new Animation(20, false); die.frames(frames, 11, 12, 13, 14);
		play(idle);
	}
}
