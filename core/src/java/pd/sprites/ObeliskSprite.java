package pd.sprites;

import pd.Assets;
import render.noosa.TextureFilm;

public class ObeliskSprite extends MobSprite {
	public ObeliskSprite() {
		texture(Assets.Sprites.SPS_OBELISK);
		TextureFilm frames = new TextureFilm(texture, 16, 16);
		idle = new Animation(3, true); idle.frames(frames, 0, 0, 0, 1, 1, 1, 2, 2, 2, 3, 3, 3);
		run = new Animation(20, true); run.frames(frames, 0);
		attack = new Animation(12, false); attack.frames(frames, 0, 2, 3);
		die = new Animation(20, false); die.frames(frames, 0);
		play(idle);
	}
}
