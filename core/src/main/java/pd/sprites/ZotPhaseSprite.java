/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.Assets;
import com.watabou.noosa.TextureFilm;

public class ZotPhaseSprite extends MobSprite {
	public ZotPhaseSprite() {
		texture(Assets.Sprites.SPS_ZOT_PHASE);
		TextureFilm frames = new TextureFilm(texture, 18, 18);
		idle = new Animation(2, true); idle.frames(frames, 0, 0, 0, 1, 0);
		run = new Animation(8, false); run.frames(frames, 0, 1, 2);
		attack = new Animation(15, false); attack.frames(frames, 0, 2, 2);
		zap = new Animation(8, false); zap.frames(frames, 2, 3, 4);
		die = new Animation(8, false); die.frames(frames, 0, 5, 6, 7, 8, 9, 8);
		play(idle);
	}
}
