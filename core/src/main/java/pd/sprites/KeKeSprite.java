/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.Assets;
import pd.effects.Lightning;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Callback;

public class KeKeSprite extends MobSprite {

	public KeKeSprite() {
		texture(Assets.Sprites.T_CLOUD);
		TextureFilm frames = new TextureFilm(texture, 16, 16);

		idle = new Animation(10, true);
		idle.frames(frames, 7, 7, 7, 10, 10, 10);
		run = new Animation(20, true);
		run.frames(frames, 6, 6, 8, 8);
		attack = new Animation(15, false);
		attack.frames(frames, 7, 10, 7, 10, 7, 10);
		die = new Animation(24, false);
		die.frames(frames, 7, 10, 10, 10);
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
