/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.Assets;
import render.noosa.TextureFilm;

public class VelociroosterSprite extends MobSprite {
	public VelociroosterSprite() {
		texture(Assets.Sprites.SPS_VELOCIROOSTER);
		TextureFilm frames = new TextureFilm(texture, 16, 16);
		idle = new Animation(2, true); idle.frames(frames, 0, 0, 1);
		run = new Animation(10, true); run.frames(frames, 3, 4, 5, 4);
		attack = new Animation(14, false); attack.frames(frames, 1, 2, 1);
		zap = attack.clone();
		die = new Animation(10, false); die.frames(frames, 1, 6, 7);
		play(idle);
	}
	@Override public int blood() { return 0xFFFFEA80; }
}
