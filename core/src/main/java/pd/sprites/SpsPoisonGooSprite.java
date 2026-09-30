/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.Assets;
import watabou.noosa.TextureFilm;

public class SpsPoisonGooSprite extends MobSprite {
	public SpsPoisonGooSprite() {
		texture(Assets.Sprites.SPS_POISON_GOO);
		TextureFilm frames = new TextureFilm(texture, 20, 14);
		idle = new Animation(10, true); idle.frames(frames, 2, 1, 0, 0, 1);
		run = new Animation(15, true); run.frames(frames, 3, 2, 1, 2);
		attack = new Animation(10, false); attack.frames(frames, 8, 9, 10);
		die = new Animation(10, false); die.frames(frames, 5, 6, 7);
		play(idle);
	}

	@Override public int blood() { return 0xFF000000; }
}
