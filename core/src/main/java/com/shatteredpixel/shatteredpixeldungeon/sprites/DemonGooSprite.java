/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.watabou.noosa.TextureFilm;

/** Original SPS-PD sprite identity, including the legacy unused pump animations. */
public class DemonGooSprite extends SpsHallsSprites.DemonGoo {

	private final Animation pump;
	private final Animation pumpAttack;

	public DemonGooSprite() {
		TextureFilm frames = new TextureFilm(texture, 20, 14);
		pump = new Animation(20, true);
		pump.frames(frames, 4, 3, 2, 1, 0);
		pumpAttack = new Animation(20, false);
		pumpAttack.frames(frames, 4, 3, 2, 1, 0, 7);
	}

	public void pumpUp() {
		play(pump);
	}

	public void pumpAttack() {
		play(pumpAttack);
	}

	@Override
	public void onComplete(Animation animation) {
		super.onComplete(animation);
		if (animation == pumpAttack) {
			idle();
			if (ch != null) ch.onAttackComplete();
		}
	}
}
