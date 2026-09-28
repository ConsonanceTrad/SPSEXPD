/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class AliveFish extends TownNpc {
	public AliveFish() {
		configure(Spec.ALIVE_FISH);
		spriteClass = com.shatteredpixel.shatteredpixeldungeon.sprites.PiranhaSprite.class;
	}
}
