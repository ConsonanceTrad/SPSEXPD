/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.sellitem;

import pd.atlas.items.SpecificPagesDict;


/** The unreadable development plan carried by the SPS author NPC. */
public class DevUpPlan extends SellItem {

	{
		image = SpecificPagesDict.GUIDE_PAGE_0;
		stackable = true;
	}

	@Override public int value() { return 500 * quantity; }
}
