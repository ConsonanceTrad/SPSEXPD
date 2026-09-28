/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class Lery extends TownNpc {
	public Lery() {
		configure(Spec.LERY);
		spriteClass = com.shatteredpixel.shatteredpixeldungeon.sprites.LerySprite.class;
	}
}
