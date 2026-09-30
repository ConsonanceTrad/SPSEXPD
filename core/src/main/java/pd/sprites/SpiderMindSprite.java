package pd.sprites;

import pd.Assets;
import watabou.noosa.TextureFilm;

public class SpiderMindSprite extends MobSprite {
	public SpiderMindSprite() { this(0); }
	protected SpiderMindSprite(int offset) {
		texture(Assets.Sprites.SPS_SPIDER_MIND);
		TextureFilm frames = new TextureFilm(texture, 16, 16);
		idle = new Animation(10, true); idle.frames(frames, offset, offset, offset, offset, offset, offset+1, offset, offset+1);
		run = new Animation(15, true); run.frames(frames, offset, offset+2, offset+3, offset+4);
		attack = new Animation(12, false); attack.frames(frames, offset+4, offset+5, offset+6, offset+7);
		die = new Animation(12, false); die.frames(frames, offset+8, offset+9, offset+10, offset+11);
		play(idle);
	}
	@Override public int blood() { return 0xFFBFE5B8; }
}
