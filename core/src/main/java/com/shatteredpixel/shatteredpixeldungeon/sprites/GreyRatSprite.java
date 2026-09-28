/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

/** Original SPS-PD grey-rat sprite identity and atlas layout. */
public class GreyRatSprite extends MobSprite {
	public GreyRatSprite() {
		texture(Assets.Sprites.SPS_RIBBON_RAT);
		TextureFilm frames = new TextureFilm(texture, 16, 15);
		idle = new Animation(2, true); idle.frames(frames, 48, 48, 48, 49);
		run = new Animation(10, true); run.frames(frames, 54, 55, 56, 57, 58);
		attack = new Animation(15, false); attack.frames(frames, 50, 51, 52, 53);
		die = new Animation(10, false); die.frames(frames, 59, 60, 61, 62);
		play(idle);
	}
}
