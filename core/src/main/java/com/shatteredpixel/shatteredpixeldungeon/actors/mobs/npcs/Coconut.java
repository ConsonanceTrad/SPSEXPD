/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class Coconut extends TownNpc {
	public Coconut() {
		configure(Spec.COCONUT);
		spriteClass = com.shatteredpixel.shatteredpixeldungeon.sprites.CoconutSprite.class;
	}
}
