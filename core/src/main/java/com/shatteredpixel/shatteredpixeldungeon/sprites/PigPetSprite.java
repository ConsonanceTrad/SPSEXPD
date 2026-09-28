/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

public class PigPetSprite extends MobSprite {
	public PigPetSprite() {
		texture(Assets.Sprites.SPS_PIG_PET);
		TextureFilm frames = new TextureFilm(texture, 16, 16);
		idle = new Animation(2, true); idle.frames(frames, 0, 1, 2, 3);
		run = new Animation(4, true); run.frames(frames, 4, 5, 6, 7);
		attack = new Animation(18, false); attack.frames(frames, 8, 9, 10, 11);
		zap = attack.clone();
		die = new Animation(6, false); die.frames(frames, 12, 13, 14, 15);
		play(idle);
	}
}
