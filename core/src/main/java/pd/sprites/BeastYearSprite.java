/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.Assets;
import render.noosa.TextureFilm;

/** Original four-frame Spring Festival year-beast animation. */
public class BeastYearSprite extends MobSprite {

	public BeastYearSprite() {
		texture(Assets.Sprites.SPS_YEAR_BEAST);
		TextureFilm frames = new TextureFilm(texture, 16, 16);
		idle = new Animation(15, true);
		idle.frames(frames, 0, 0, 1, 1, 0, 0);
		run = new Animation(20, true);
		run.frames(frames, 0, 0);
		attack = new Animation(12, false);
		attack.frames(frames, 1, 1, 2, 2, 2, 3, 3);
		die = new Animation(20, false);
		die.frames(frames, 2, 2, 1, 1, 0, 0);
		play(idle);
	}
}
