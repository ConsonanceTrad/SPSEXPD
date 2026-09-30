/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.Assets;
import com.watabou.noosa.TextureFilm;

public class SewerLasherSprite extends MobSprite {
	public SewerLasherSprite() {
		texture(Assets.Sprites.SPS_SEWER_LASHER);
		TextureFilm frames = new TextureFilm(texture, 12, 16);
		idle = new Animation(1, true); idle.frames(frames, 0);
		run = new Animation(1, true); run.frames(frames, 0);
		attack = new Animation(24, false); attack.frames(frames, 0, 1, 2, 2, 1);
		die = new Animation(12, false); die.frames(frames, 3, 4, 5, 6);
		play(idle);
	}
}
