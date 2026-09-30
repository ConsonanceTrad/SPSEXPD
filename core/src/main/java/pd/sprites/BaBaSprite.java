/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.Assets;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Random;

/** The armed sheep frames stored on the second row of SPS-PD's sheep sheet. */
public class BaBaSprite extends MobSprite {

	public BaBaSprite() {
		texture(Assets.Sprites.SHEEP);
		TextureFilm frames = new TextureFilm(texture, 16, 15);

		idle = new Animation(8, true);
		idle.frames(frames, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 6, 7, 4);
		run = idle.clone();
		attack = new Animation(12, false);
		attack.frames(frames, 6, 7, 4, 5);
		die = new Animation(20, false);
		die.frames(frames, 4);

		play(idle);
		curFrame = Random.Int(curAnim.frames.length);
	}
}
