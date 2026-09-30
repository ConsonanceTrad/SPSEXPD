package pd.sprites;

import pd.Assets;
import watabou.noosa.TextureFilm;

public class SpiderGoldSprite extends MobSprite {
	public SpiderGoldSprite() {
		texture(Assets.Sprites.SPS_SPIDER_MIND);
		TextureFilm frames = new TextureFilm(texture, 16, 16);
		idle = new Animation(10, true); idle.frames(frames, 16, 16, 16, 16, 16, 17, 16, 17);
		run = new Animation(15, true); run.frames(frames, 16, 18, 19, 20);
		attack = new Animation(12, false); attack.frames(frames, 20, 21, 22, 23);
		die = new Animation(12, false); die.frames(frames, 24, 25, 25, 25);
		play(idle);
	}
	@Override public int blood() { return 0xFFBFE5B8; }
}
