/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.Dungeon;
import pd.sprites.ItemSpriteSheet;

public class AncientCoin extends SpsBossKey {
	{ image = ItemSpriteSheet.ANCIENT_COIN; }
	@Override protected int destination() { return 13; }
	@Override protected boolean bossKilled() { return Dungeon.banditKingKilled; }
}
