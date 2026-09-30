/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.Assets;
import watabou.noosa.TextureFilm;

public class PrisonWanderSprite extends MobSprite {
	public PrisonWanderSprite() {
		texture(Assets.Sprites.SPS_PRISON_WANDER);
		TextureFilm frames = new TextureFilm(texture, 12, 16);
		idle = new Animation(2, true); idle.frames(frames, 0, 0, 0, 1, 0, 0, 1, 1);
		run = new Animation(15, true); run.frames(frames, 2, 3, 4, 5, 6, 7);
		attack = new Animation(12, false); attack.frames(frames, 8, 9, 10);
		die = new Animation(8, false); die.frames(frames, 11, 12, 13, 14);
		play(idle);
	}
}
