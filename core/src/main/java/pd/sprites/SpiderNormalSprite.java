package pd.sprites;

import pd.Assets;
import watabou.noosa.TextureFilm;

public class SpiderNormalSprite extends MobSprite {
	public SpiderNormalSprite() { this(0); }
	protected SpiderNormalSprite(int offset) {
		texture(Assets.Sprites.SPS_SPIDER_WORKER);
		TextureFilm frames = new TextureFilm(texture, 16, 16);
		idle = new Animation(10, true); idle.frames(frames, offset, offset, offset, offset, offset+1, offset+2, offset, offset+1);
		run = new Animation(15, true); run.frames(frames, offset+2, offset+3, offset+4, offset+5);
		attack = new Animation(12, false); attack.frames(frames, offset+6, offset+7, offset+8, offset+9);
		die = new Animation(12, false); die.frames(frames, offset+10, offset+11, offset+12, offset+13);
		play(idle);
	}
	@Override public int blood() { return 0xFFBFE5B8; }
}
