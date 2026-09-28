/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

public class SpsBossDragonSprite extends MobSprite {
	public SpsBossDragonSprite() {
		texture(Assets.Sprites.SPS_BOSS_DRAGON);
		TextureFilm frames = new TextureFilm(texture, 22, 20);
		idle = new Animation(4, true); idle.frames(frames, 0, 0, 0, 0, 1, 1, 1, 1);
		run = new Animation(10, true); run.frames(frames, 2, 3);
		attack = new Animation(15, false); attack.frames(frames, 4, 5, 6);
		die = new Animation(20, false); die.frames(frames, 0, 7, 0, 7, 0, 7, 0, 7, 8);
		play(idle);
	}

	@Override
	public int blood() {
		return 0xFFFFFF88;
	}
}
