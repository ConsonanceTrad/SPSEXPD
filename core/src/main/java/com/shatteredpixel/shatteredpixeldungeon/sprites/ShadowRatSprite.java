/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

public class ShadowRatSprite extends MobSprite {
	public ShadowRatSprite() {
		texture(Assets.Sprites.SPS_SHADOW_RAT);
		TextureFilm frames = new TextureFilm(texture, 16, 15);
		idle = new Animation(2, true); idle.frames(frames, 64, 64, 64, 65);
		run = new Animation(10, true); run.frames(frames, 70, 71, 72, 73, 74);
		attack = new Animation(15, false); attack.frames(frames, 66, 67, 68, 64);
		die = new Animation(10, false); die.frames(frames, 75, 76, 77, 78);
		play(idle);
	}
}
