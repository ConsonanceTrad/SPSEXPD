/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class Bilboldev extends TownNpc {
	public Bilboldev() {
		configure(Spec.BILBOLDEV);
		spriteClass = com.shatteredpixel.shatteredpixeldungeon.sprites.BilboldevSprite.class;
	}
}
