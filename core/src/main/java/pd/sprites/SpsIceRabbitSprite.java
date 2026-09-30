/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.Assets;
import com.watabou.noosa.TextureFilm;

public class SpsIceRabbitSprite extends MobSprite {
	protected SpsIceRabbitSprite(int first) {
		texture(Assets.Sprites.SPS_ICE_RABBIT);
		TextureFilm frames = new TextureFilm(texture, 16, 16);
		if (first == 0) {
			idle = new Animation(3, true); idle.frames(frames, 0, 0, 1, 1, 2, 2, 3, 3);
			run = new Animation(20, true); run.frames(frames, 3, 4, 4, 5, 6, 6, 7);
			attack = new Animation(20, false); attack.frames(frames, 7, 7, 8, 9, 10, 11);
			die = new Animation(20, false); die.frames(frames, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22);
		} else {
			idle = new Animation(3, true); idle.frames(frames, 23, 23, 24, 24);
			run = new Animation(20, true); run.frames(frames, 25, 26, 27, 28, 29, 30, 31);
			attack = new Animation(20, false); attack.frames(frames, 31, 32, 33, 34, 35, 36, 37);
			die = new Animation(20, false); die.frames(frames, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47);
		}
		zap = attack.clone();
		play(idle);
	}

	public SpsIceRabbitSprite() { this(0); }
	public static class Final extends SpsIceRabbitSprite { public Final() { super(23); } }
}
