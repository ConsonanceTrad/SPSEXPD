/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.Assets;
import render.noosa.TextureFilm;

public class GnollKeeperSprite extends MobSprite {
	public GnollKeeperSprite() {
		texture(Assets.Sprites.SPS_GNOLL_KING);
		TextureFilm frames = new TextureFilm(texture, 12, 16);
		idle = new Animation(2, true); idle.frames(frames, 21, 21, 21, 22, 21, 21, 22, 22);
		run = new Animation(12, true); run.frames(frames, 25, 26, 27, 28);
		attack = new Animation(12, false); attack.frames(frames, 23, 24);
		die = new Animation(12, false); die.frames(frames, 29, 30, 31);
		play(idle);
	}
}
