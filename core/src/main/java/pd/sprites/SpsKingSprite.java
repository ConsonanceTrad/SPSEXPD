package pd.sprites;

import pd.Assets;
import render.noosa.TextureFilm;

public class SpsKingSprite extends MobSprite {
	public SpsKingSprite() {
		texture(Assets.Sprites.SPS_KING);
		TextureFilm frames = new TextureFilm(texture, 16, 16);
		idle = new Animation(5, true); idle.frames(frames, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 2, 2, 1, 1, 1);
		run = new Animation(15, true); run.frames(frames, 3, 4, 5, 6, 7, 8);
		attack = new Animation(15, false); attack.frames(frames, 9, 10, 11, 12, 13, 14, 15);
		die = new Animation(10, false); die.frames(frames, 21, 22, 23, 24);
		play(idle);
	}
}
