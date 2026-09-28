/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class WhiteGhost extends TownNpc {
	public WhiteGhost() {
		configure(Spec.WHITE_GHOST);
		spriteClass = com.shatteredpixel.shatteredpixeldungeon.sprites.WhiteGhostSprite.class;
	}
}
