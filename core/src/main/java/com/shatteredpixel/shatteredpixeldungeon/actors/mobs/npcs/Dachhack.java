/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class Dachhack extends TownNpc {
	public Dachhack() {
		configure(Spec.DACHHACK);
		spriteClass = com.shatteredpixel.shatteredpixeldungeon.sprites.DachhackSprite.class;
	}
}
