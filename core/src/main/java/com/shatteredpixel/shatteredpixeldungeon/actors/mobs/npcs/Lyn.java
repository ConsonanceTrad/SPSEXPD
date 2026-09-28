/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class Lyn extends TownNpc {
	public Lyn() {
		configure(Spec.LYN);
		spriteClass = com.shatteredpixel.shatteredpixeldungeon.sprites.LynSprite.class;
	}
}
