/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.Assets;
import com.watabou.noosa.TextureFilm;

public class SpsErrorSprite extends MobSprite {
	public SpsErrorSprite() {
		texture(Assets.Sprites.SPS_ERROR);
		TextureFilm frames = new TextureFilm(texture, 16, 16);
		idle = new Animation(3, true); idle.frames(frames, 0, 0, 0, 1, 1, 1, 2, 2, 2, 3, 3, 3);
		run = new Animation(20, true); run.frames(frames, 0);
		attack = new Animation(12, false); attack.frames(frames, 0, 2, 3);
		die = new Animation(20, false); die.frames(frames, 0);
		play(idle);
	}
}
