/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class AncientCoin extends SpsBossKey {
	{ image = ItemSpriteSheet.ANCIENT_COIN; }
	@Override protected int destination() { return 13; }
	@Override protected boolean bossKilled() { return Dungeon.banditKingKilled; }
}
