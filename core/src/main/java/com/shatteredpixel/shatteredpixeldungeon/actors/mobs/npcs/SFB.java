/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class SFB extends TownNpc {
	public SFB() {
		configure(Spec.SFB);
		spriteClass = com.shatteredpixel.shatteredpixeldungeon.sprites.SFBSprite.class;
	}
}
