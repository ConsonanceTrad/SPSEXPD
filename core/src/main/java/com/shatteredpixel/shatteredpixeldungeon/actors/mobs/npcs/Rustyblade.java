/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class Rustyblade extends TownNpc {
	public Rustyblade() {
		configure(Spec.RUSTYBLADE);
		spriteClass = com.shatteredpixel.shatteredpixeldungeon.sprites.RustybladeSprite.class;
	}
}
