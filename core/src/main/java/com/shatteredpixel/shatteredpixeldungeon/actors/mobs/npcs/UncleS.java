/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class UncleS extends TownNpc {
	public UncleS() {
		configure(Spec.UNCLE_S);
		spriteClass = com.shatteredpixel.shatteredpixeldungeon.sprites.UncleSSprite.class;
	}
}
