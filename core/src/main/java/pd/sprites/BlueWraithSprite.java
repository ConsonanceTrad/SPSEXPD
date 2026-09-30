/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.Assets;
import watabou.noosa.TextureFilm;

public class BlueWraithSprite extends MobSprite {

	public BlueWraithSprite() {
		texture(Assets.Sprites.SPS_BLUE_WRAITH);
		TextureFilm frames = new TextureFilm(texture, 14, 15);
		idle = new Animation(5, true);
		idle.frames(frames, 0, 1);
		run = new Animation(10, true);
		run.frames(frames, 0, 1);
		attack = new Animation(10, false);
		attack.frames(frames, 0, 2, 3);
		die = new Animation(8, false);
		die.frames(frames, 0, 4, 5, 6, 7);
		play(idle);
	}

	@Override public int blood() { return 0x88000000; }
}
