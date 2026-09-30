/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.Assets;
import com.watabou.noosa.TextureFilm;

public class SkeletonHand2Sprite extends MobSprite {
	public SkeletonHand2Sprite() {
		texture(Assets.Sprites.SPS_SKELETON_HAND);
		TextureFilm frames = new TextureFilm(texture, 16, 16);
		idle = new Animation(2, true); idle.frames(frames, 0, 1);
		run = new Animation(4, true); run.frames(frames, 0, 1);
		attack = new Animation(15, false); attack.frames(frames, 2, 3, 4, 5);
		die = new Animation(5, false); die.frames(frames, 6, 7, 8, 9);
		play(idle);
	}
}
