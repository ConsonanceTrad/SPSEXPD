/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.Dungeon;
import pd.sprites.ItemSpriteSheet;

public class ConchShell extends SpsBossKey {
	{ image = ItemSpriteSheet.CAVE_SHELL; }
	@Override protected int destination() { return 12; }
	@Override protected boolean bossKilled() { return Dungeon.crabKingKilled; }
}
