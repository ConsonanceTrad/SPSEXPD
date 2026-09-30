package pd.sprites;

import pd.Assets;
import com.watabou.noosa.TextureFilm;

/** The second row of the original SPS troll smith sprite sheet. */
public class ElectricwelderSprite extends MobSprite {

	public ElectricwelderSprite() {
		texture(Assets.Sprites.TROLL);
		TextureFilm frames = new TextureFilm(texture, 13, 16);

		idle = new Animation(15, true);
		idle.frames(frames, 4, 4, 4, 4, 4, 4, 4, 5, 6, 6, 6, 7);
		run = new Animation(20, true);
		run.frames(frames, 4);
		die = new Animation(20, false);
		die.frames(frames, 4);

		play(idle);
	}
}
