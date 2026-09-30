/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.Assets;
import watabou.noosa.TextureFilm;

public class HybridSprite extends MobSprite {
	public HybridSprite() {
		texture(Assets.Sprites.SPS_HYBRID);
		TextureFilm frames = new TextureFilm(texture, 22, 18);
		idle = new Animation(10, true); idle.frames(frames, 0, 1);
		run = new Animation(10, true); run.frames(frames, 2, 3);
		attack = new Animation(15, false); attack.frames(frames, 4, 5, 6, 7);
		die = new Animation(4, false); die.frames(frames, 0, 8, 9, 10);
		play(idle);
	}
}
