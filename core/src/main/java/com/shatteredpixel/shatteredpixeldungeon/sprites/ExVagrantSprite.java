/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;

/** Original SPS-PD infected vagrant sprite identity, including its death particles. */
public class ExVagrantSprite extends SpsSewerSprites.ExVagrant {
	@Override
	public void die() {
		super.die();
		if (ch != null && Dungeon.level != null && Dungeon.level.insideMap(ch.pos)
				&& Dungeon.level.heroFOV[ch.pos]) emitter().burst(Speck.factory(Speck.STAR), 6);
	}
}
