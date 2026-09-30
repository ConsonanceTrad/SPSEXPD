/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.Dungeon;
import pd.sprites.ItemSpriteSheet;

public class Bone extends SpsBossKey {
	{ image = ItemSpriteSheet.KING_BONE; }
	@Override protected int destination() { return 11; }
	@Override protected boolean bossKilled() { return Dungeon.skeletonKingKilled; }
}
