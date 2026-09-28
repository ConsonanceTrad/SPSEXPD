/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class CatSheep extends TownNpc {
	public CatSheep() {
		configure(Spec.CAT_SHEEP);
		spriteClass = com.shatteredpixel.shatteredpixeldungeon.sprites.CatSheepSprite.class;
	}
}
