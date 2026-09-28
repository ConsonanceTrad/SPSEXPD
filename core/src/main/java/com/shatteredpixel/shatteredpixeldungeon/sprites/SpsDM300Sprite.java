/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.watabou.noosa.TextureFilm;

public class SpsDM300Sprite extends MobSprite {
	public SpsDM300Sprite() {
		texture(Assets.Sprites.SPS_DM300);
		TextureFilm frames = new TextureFilm(texture, 22, 20);
		idle = new Animation(10, true); idle.frames(frames, 0, 1);
		run = new Animation(10, true); run.frames(frames, 2, 3);
		attack = new Animation(15, false); attack.frames(frames, 4, 5, 6);
		die = new Animation(20, false); die.frames(frames, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 8);
		play(idle);
	}

	@Override public int blood() { return 0xFFFFFF88; }
	@Override public void onComplete(Animation anim) {
		super.onComplete(anim);
		if (anim == die) emitter().burst(Speck.factory(Speck.WOOL), 15);
	}
}
