/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class BlackMeow extends TownNpc {
	public BlackMeow() {
		configure(Spec.BLACK_MEOW);
		spriteClass = com.shatteredpixel.shatteredpixeldungeon.sprites.BlackMeowSprite.class;
	}
}
