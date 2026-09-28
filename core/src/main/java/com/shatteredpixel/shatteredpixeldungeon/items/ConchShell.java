/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class ConchShell extends SpsBossKey {
	{ image = ItemSpriteSheet.CAVE_SHELL; }
	@Override protected int destination() { return 12; }
	@Override protected boolean bossKilled() { return Dungeon.crabKingKilled; }
}
