/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class AFly extends TownNpc {
	public AFly() {
		configure(Spec.A_FLY);
		spriteClass = com.shatteredpixel.shatteredpixeldungeon.sprites.AFlySprite.class;
	}
}
