/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.Assets;
import render.noosa.TextureFilm;

public class SpsSeekingBombSprite extends MobSprite {
	public SpsSeekingBombSprite() {
		texture(Assets.Sprites.SPS_SEEKING_BOMB);
		TextureFilm frames = new TextureFilm(texture, 16, 16);
		idle = new Animation(5, true); idle.frames(frames, 0, 0, 0, 0);
		run = new Animation(15, true); run.frames(frames, 1, 2, 3, 4);
		attack = new Animation(12, false); attack.frames(frames, 1, 2, 3);
		die = new Animation(12, false); die.frames(frames, 4, 4, 4, 4);
		play(idle);
	}
	@Override public int blood() { return 0xFFFFEA80; }
}
