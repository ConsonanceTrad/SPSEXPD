/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.Assets;
import pd.effects.Lightning;
import render.noosa.TextureFilm;
import render.utils.data.Callback;

public class TCloudSprite extends MobSprite {

	public TCloudSprite() {
		texture(Assets.Sprites.T_CLOUD);
		TextureFilm frames = new TextureFilm(texture, 16, 16);

		idle = new Animation(10, true);
		idle.frames(frames, 0, 0, 3, 3, 4, 4, 3, 3, 0, 0);
		run = new Animation(20, true);
		run.frames(frames, 0, 0, 3, 3, 4, 4, 3, 3, 0, 0);
		attack = new Animation(12, false);
		attack.frames(frames, 0, 1, 2, 3);
		die = new Animation(20, false);
		die.frames(frames, 0, 4, 5);
		zap = attack.clone();
		play(idle);
	}

	@Override
	public void zap(int pos) {
		parent.add(new Lightning(ch.pos, pos, (Callback)ch));
		turnTo(ch.pos, pos);
		play(zap);
	}
}
