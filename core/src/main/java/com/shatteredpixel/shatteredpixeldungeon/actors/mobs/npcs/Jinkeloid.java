/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class Jinkeloid extends TownNpc {
	public Jinkeloid() {
		configure(Spec.JINKELOID);
		spriteClass = com.shatteredpixel.shatteredpixeldungeon.sprites.JinkeloidSprite.class;
	}
}
