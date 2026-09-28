/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Bone extends SpsBossKey {
	{ image = ItemSpriteSheet.KING_BONE; }
	@Override protected int destination() { return 11; }
	@Override protected boolean bossKilled() { return Dungeon.skeletonKingKilled; }
}
