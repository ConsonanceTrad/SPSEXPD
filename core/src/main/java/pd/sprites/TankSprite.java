/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.Assets;
import render.noosa.TextureFilm;

public class TankSprite extends MobSprite {
	public TankSprite() {
		texture(Assets.Sprites.SPS_TANK);
		TextureFilm frames = new TextureFilm(texture, 16, 14);
		idle = new Animation(2, true); idle.frames(frames, 0, 0, 0, 1);
		run = new Animation(10, true); run.frames(frames, 6, 7, 8, 9, 10);
		attack = new Animation(15, false); attack.frames(frames, 2, 3, 4, 5, 0);
		die = new Animation(10, false); die.frames(frames, 11, 12, 13, 14);
		play(idle);
	}
}
