/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.Assets;
import render.noosa.TextureFilm;

public class SpsFireRabbitSprite extends MobSprite {
	public SpsFireRabbitSprite() {
		texture(Assets.Sprites.SPS_FIRE_RABBIT);
		TextureFilm frames = new TextureFilm(texture, 16, 16);
		idle = new Animation(3, true); idle.frames(frames, 0, 0, 0, 1, 1, 1);
		run = new Animation(20, true); run.frames(frames, 0, 2, 3, 4, 5, 6, 7);
		attack = new Animation(20, false); attack.frames(frames, 0, 8, 9, 9, 9, 10);
		zap = attack.clone();
		die = new Animation(20, false); die.frames(frames, 0, 11, 11, 12, 12, 13);
		play(idle);
	}
}
