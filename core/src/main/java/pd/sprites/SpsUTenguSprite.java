/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.Assets;
import render.noosa.TextureFilm;

public class SpsUTenguSprite extends MobSprite {
	public SpsUTenguSprite() {
		texture(Assets.Sprites.TENGU);
		TextureFilm frames = new TextureFilm(texture, 14, 16);
		idle = new Animation(2, true); idle.frames(frames, 11, 11, 11, 12);
		run = new Animation(15, false); run.frames(frames, 13, 14, 15, 16, 11);
		attack = new Animation(15, false); attack.frames(frames, 17, 18, 18, 11);
		zap = attack.clone();
		die = new Animation(8, false); die.frames(frames, 19, 20, 21, 21, 21, 21, 21, 21);
		play(run.clone());
	}
}
