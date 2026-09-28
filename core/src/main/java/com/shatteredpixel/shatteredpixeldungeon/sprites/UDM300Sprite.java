/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;

/** Original SPS-PD 0.9.8 DM-300 variant sprite identity. */
public class UDM300Sprite extends SpsUDM300Sprite {
	@Override
	public void onComplete(Animation animation) {
		super.onComplete(animation);
		if (animation == die) emitter().burst(Speck.factory(Speck.WOOL), 15);
	}
}
