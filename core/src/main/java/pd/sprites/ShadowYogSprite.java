/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.Assets;
import pd.effects.Splash;
import render.noosa.TextureFilm;

public class ShadowYogSprite extends MobSprite {

	public ShadowYogSprite() {
		texture(Assets.Sprites.SPS_SHADOW_YOG);
		TextureFilm frames = new TextureFilm(texture, 20, 19);
		idle = new Animation(10, true);
		idle.frames(frames, 0, 1, 2, 2, 1, 0, 3, 4, 4, 3, 0, 5, 6, 6, 5);
		run = new Animation(12, true);
		run.frames(frames, 0);
		attack = new Animation(15, false);
		attack.frames(frames, 0);
		zap = attack.clone();
		die = new Animation(10, false);
		die.frames(frames, 0, 7, 8, 9);
		play(idle);
	}

	@Override
	public void die() {
		super.die();
		Splash.at(center(), blood(), 12);
	}
}
