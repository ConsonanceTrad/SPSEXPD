/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class MemoryOfSand extends TownNpc {
	public MemoryOfSand() {
		configure(Spec.MEMORY_OF_SAND);
		spriteClass = com.shatteredpixel.shatteredpixeldungeon.sprites.MemoryOfSandSprite.class;
	}
}
