/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

/** Original SPS runtime and save identity for this town resident. */
public class StormAndRain extends TownNpc {
	public StormAndRain() {
		configure(Spec.STORM_AND_RAIN);
		spriteClass = com.shatteredpixel.shatteredpixeldungeon.sprites.StormAndRainSprite.class;
	}
}
