/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class Ravenwolf extends TownNpc {
	public Ravenwolf() {
		configure(Spec.RAVENWOLF);
		spriteClass = com.shatteredpixel.shatteredpixeldungeon.sprites.RavenwolfSprite.class;
	}
}
