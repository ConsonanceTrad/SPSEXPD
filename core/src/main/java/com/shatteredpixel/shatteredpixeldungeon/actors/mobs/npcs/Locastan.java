/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class Locastan extends TownNpc {
	public Locastan() {
		configure(Spec.LOCASTAN);
		spriteClass = com.shatteredpixel.shatteredpixeldungeon.sprites.LocastanSprite.class;
	}
}
