/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class OldNewStwist extends TownNpc {
	public OldNewStwist() {
		configure(Spec.OLD_NEW_STWIST);
		spriteClass = com.shatteredpixel.shatteredpixeldungeon.sprites.OldNewStwistSprite.class;
	}
}
