/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.Assets;
import com.watabou.noosa.TextureFilm;

public class HaroSprite extends MobSprite {
	public HaroSprite() {
		texture(Assets.Sprites.SPS_HARO);
		TextureFilm frames = new TextureFilm(texture, 16, 16);
		idle = new Animation(10, true); idle.frames(frames, 0, 0, 0, 1, 1, 1, 2, 2);
		run = new Animation(20, true); run.frames(frames, 3, 3, 4, 4);
		attack = new Animation(12, false); attack.frames(frames, 4, 4, 3, 3, 5, 5);
		zap = attack.clone();
		die = new Animation(20, false); die.frames(frames, 0, 6, 7);
		play(idle);
	}
}
