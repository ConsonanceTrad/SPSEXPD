/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class Apostle extends TownNpc {
	public Apostle() {
		configure(Spec.APOSTLE);
		spriteClass = com.shatteredpixel.shatteredpixeldungeon.sprites.ApostleSprite.class;
	}
}
