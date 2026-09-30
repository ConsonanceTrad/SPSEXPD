package pd.sprites;

import pd.Assets;
import watabou.noosa.TextureFilm;

public class SpiderQueenSprite extends MobSprite {
	public SpiderQueenSprite() {
		texture(Assets.Sprites.SPS_SPIDER_QUEEN);
		TextureFilm frames = new TextureFilm(texture, 16, 16);
		idle = new Animation(10, true); idle.frames(frames, 0, 0, 1, 2, 1, 0, 0, 1);
		run = new Animation(15, true); run.frames(frames, 2, 2, 3, 4);
		attack = new Animation(12, false); attack.frames(frames, 4, 5, 6, 7);
		die = new Animation(12, false); die.frames(frames, 8, 9, 10, 11);
		play(idle);
	}
	@Override public int blood() { return 0xFFBFE5B8; }
}
