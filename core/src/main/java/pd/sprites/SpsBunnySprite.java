/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.Assets;
import render.noosa.TextureFilm;

public class SpsBunnySprite extends MobSprite {
	public SpsBunnySprite() {
		texture(Assets.Sprites.SPS_BUNNY);
		TextureFilm frames = new TextureFilm(texture, 14, 16);
		idle = new Animation(2, true); idle.frames(frames, 0, 1, 0, 0);
		run = new Animation(4, true); run.frames(frames, 0, 0, 0, 2);
		attack = new Animation(15, false); attack.frames(frames, 0, 2, 3, 4);
		zap = attack.clone();
		die = new Animation(8, false); die.frames(frames, 5, 6, 7, 8);
		play(idle);
	}
	@Override public int blood() { return 0xFFcdcdb7; }
}
