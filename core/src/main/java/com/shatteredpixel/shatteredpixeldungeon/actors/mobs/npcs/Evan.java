/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class Evan extends TownNpc {
	public Evan() {
		configure(Spec.EVAN);
		spriteClass = com.shatteredpixel.shatteredpixeldungeon.sprites.EvanSprite.class;
	}
}
