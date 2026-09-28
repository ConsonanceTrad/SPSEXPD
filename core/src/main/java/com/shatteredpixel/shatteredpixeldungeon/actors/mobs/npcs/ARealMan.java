/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class ARealMan extends TownNpc {
	public ARealMan() {
		configure(Spec.A_REAL_MAN);
		spriteClass = com.shatteredpixel.shatteredpixeldungeon.sprites.ARealManSprite.class;
	}
}
