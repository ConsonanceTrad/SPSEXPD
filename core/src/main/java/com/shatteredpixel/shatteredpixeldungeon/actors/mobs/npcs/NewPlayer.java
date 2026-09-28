/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class NewPlayer extends TownNpc {
	public NewPlayer() {
		configure(Spec.NEW_PLAYER);
		spriteClass = com.shatteredpixel.shatteredpixeldungeon.sprites.NewPlayerSprite.class;
	}
}
