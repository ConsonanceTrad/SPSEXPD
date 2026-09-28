/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

public class PlagueDoctorSprite extends MobSprite {
	public PlagueDoctorSprite() {
		texture(Assets.Sprites.SPS_PLAGUE_DOCTOR);
		TextureFilm frames = new TextureFilm(texture, 16, 16);
		idle = new Animation(2, true); idle.frames(frames, 0, 0, 1, 1);
		run = new Animation(10, true); run.frames(frames, 1, 2, 2, 3, 2, 1);
		attack = new Animation(15, false); attack.frames(frames, 4, 5, 6, 7, 8);
		die = new Animation(10, false); die.frames(frames, 8, 8, 9, 10);
		play(idle);
	}
}
