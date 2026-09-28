/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

public class DwarfBoySprite extends MobSprite {
	public DwarfBoySprite() {
		texture(Assets.Sprites.SPS_DWARF_BOY);
		TextureFilm frames = new TextureFilm(texture, 16, 16);
		idle = new Animation(2, true); idle.frames(frames, 0, 1, 2, 3);
		run = new Animation(4, true); run.frames(frames, 4, 5, 6, 7);
		attack = new Animation(15, false); attack.frames(frames, 8, 9, 10, 11);
		zap = attack.clone();
		die = new Animation(6, false); die.frames(frames, 12, 13, 14, 15);
		play(idle);
	}
	@Override public int blood() { return 0xFFcdcdb7; }
}
