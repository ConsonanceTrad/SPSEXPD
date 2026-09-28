/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class HeXA extends TownNpc {
	public HeXA() {
		configure(Spec.HEXA);
		spriteClass = com.shatteredpixel.shatteredpixeldungeon.sprites.HeXASprite.class;
	}
}
