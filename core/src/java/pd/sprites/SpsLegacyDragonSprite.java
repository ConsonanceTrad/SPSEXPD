/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import render.noosa.TextureFilm;

import java.util.Calendar;

/** Shared frame layout used by the original elemental dragon sheets. */
abstract class SpsLegacyDragonSprite extends MobSprite {

	SpsLegacyDragonSprite(String texture, boolean alwaysFirstRow) {
		super();
		texture(texture);
		int first = alwaysFirstRow || Calendar.getInstance().get(Calendar.MONTH) == Calendar.OCTOBER ? 0 : 16;
		TextureFilm frames = new TextureFilm(this.texture, 16, 16);
		idle = new Animation(2, true);
		idle.frames(frames, first, first + 1, first + 2, first + 3);
		run = new Animation(8, true);
		run.frames(frames, first + 4, first + 5, first + 6, first + 7);
		attack = new Animation(15, false);
		attack.frames(frames, first + 8, first + 9, first + 10, first + 11);
		zap = attack.clone();
		die = new Animation(8, false);
		die.frames(frames, first + 12, first + 13, first + 14, first + 15);
		play(idle);
	}

	@Override
	public int blood() {
		return 0xFFcdcdb7;
	}
}
