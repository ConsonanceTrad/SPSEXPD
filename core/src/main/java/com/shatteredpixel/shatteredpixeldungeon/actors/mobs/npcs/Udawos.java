/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class Udawos extends TownNpc {
	public Udawos() {
		configure(Spec.UDAWOS);
		spriteClass = com.shatteredpixel.shatteredpixeldungeon.sprites.UdawosSprite.class;
	}
}
