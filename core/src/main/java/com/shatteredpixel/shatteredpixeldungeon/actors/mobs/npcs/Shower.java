/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class Shower extends TownNpc {
	public Shower() {
		configure(Spec.SHOWER);
		spriteClass = com.shatteredpixel.shatteredpixeldungeon.sprites.ShowerSprite.class;
	}
}
