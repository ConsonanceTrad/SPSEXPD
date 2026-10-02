/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;

public class ConchShell extends SpsBossKey {
	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override protected int destination() { return 12; }
	@Override protected boolean bossKilled() { return Dungeon.crabKingKilled; }
}
