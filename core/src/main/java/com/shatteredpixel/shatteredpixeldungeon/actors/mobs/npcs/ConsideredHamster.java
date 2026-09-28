/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class ConsideredHamster extends TownNpc {
	public ConsideredHamster() {
		configure(Spec.CONSIDERED_HAMSTER);
		spriteClass = com.shatteredpixel.shatteredpixeldungeon.sprites.MimicSprite.class;
	}
}
