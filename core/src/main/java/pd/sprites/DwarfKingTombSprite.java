package pd.sprites;

import pd.Assets;
import com.watabou.noosa.TextureFilm;

public class DwarfKingTombSprite extends MobSprite {
	public DwarfKingTombSprite() {
		texture(Assets.Sprites.SPS_DWARF_KING_TOMB);
		TextureFilm frames = new TextureFilm(texture, 16, 16);
		idle = new Animation(10, true); idle.frames(frames, 0);
		run = idle.clone(); attack = idle.clone(); die = idle.clone();
		play(idle);
	}
}
