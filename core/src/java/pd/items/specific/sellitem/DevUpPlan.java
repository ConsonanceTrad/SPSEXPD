/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.specific.sellitem;

import pd.atlas.items.SpecificPagesDict;
import pd.messages.InlineText;


/** The unreadable development plan carried by the SPS author NPC. */
public class DevUpPlan extends SellItem {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(DevUpPlan.class)
			.t("name", "更新计划")
			.t("desc", "厚厚的本子里面涂满了乱七八糟的东西，其中大部分被打上了钩。");
	}




	{
		image = SpecificPagesDict.GUIDE_PAGE_0;
		stackable = true;
	}

	@Override public int value() { return 500 * quantity; }
}
