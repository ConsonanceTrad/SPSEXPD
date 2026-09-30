/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.Assets;
import com.watabou.noosa.TextureFilm;

public class SpsUDM300Sprite extends MobSprite {
	public SpsUDM300Sprite() {
		texture(Assets.Sprites.SPS_UDM300);
		TextureFilm frames = new TextureFilm(texture, 22, 20);
		idle = new Animation(10, true); idle.frames(frames, 9, 10);
		run = new Animation(10, true); run.frames(frames, 11, 12);
		attack = new Animation(15, false); attack.frames(frames, 13, 14, 15);
		die = new Animation(20, false); die.frames(frames, 9, 16, 9, 16, 9, 16, 9, 16, 17);
		play(idle);
	}
	@Override public int blood() { return 0xFFFFFF88; }
}
