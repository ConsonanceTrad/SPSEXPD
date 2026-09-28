/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class FruitCat extends TownNpc {
	public FruitCat() {
		configure(Spec.FRUIT_CAT);
		spriteClass = com.shatteredpixel.shatteredpixeldungeon.sprites.FruitCatSprite.class;
	}
}
