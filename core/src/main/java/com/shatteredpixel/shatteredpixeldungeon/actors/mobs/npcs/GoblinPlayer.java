/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class GoblinPlayer extends TownNpc {
	public GoblinPlayer() {
		configure(Spec.GOBLIN_PLAYER);
		spriteClass = com.shatteredpixel.shatteredpixeldungeon.sprites.GoblinPlayerSprite.class;
	}
}
