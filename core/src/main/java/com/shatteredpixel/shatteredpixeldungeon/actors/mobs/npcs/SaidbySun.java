/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class SaidbySun extends TownNpc {
	public SaidbySun() {
		configure(Spec.SAID_BY_SUN);
		spriteClass = com.shatteredpixel.shatteredpixeldungeon.sprites.SaidbySunSprite.class;
	}
}
