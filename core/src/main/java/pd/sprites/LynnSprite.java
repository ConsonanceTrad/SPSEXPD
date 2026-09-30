/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import render.noosa.TextureFilm;

/** Original Lynn animation, reused by her summoned curse doll. */
public class LynnSprite extends MobSprite {

	public LynnSprite() {
		texture("sprites/npcs/sps_town_lynn.png");
		TextureFilm frames = new TextureFilm(texture, 16, 16);
		idle = new Animation(3, true);
		idle.frames(frames, 0, 0, 0, 1, 1, 1, 2, 2, 2, 3, 3, 3);
		run = new Animation(20, true);
		run.frames(frames, 0);
		attack = new Animation(12, false);
		attack.frames(frames, 0, 2, 3);
		die = new Animation(20, false);
		die.frames(frames, 0);
		play(idle);
	}
}
