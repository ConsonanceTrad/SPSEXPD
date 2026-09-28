/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class HateSokoban extends TownNpc {
	public HateSokoban() {
		configure(Spec.HATE_SOKOBAN);
		spriteClass = com.shatteredpixel.shatteredpixeldungeon.sprites.HateSokobanSprite.class;
	}
}
