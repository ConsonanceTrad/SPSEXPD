/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class NYRDS extends TownNpc {
	public NYRDS() {
		configure(Spec.NYRDS);
		spriteClass = com.shatteredpixel.shatteredpixeldungeon.sprites.NYRDSSprite.class;
	}
}
