/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;


public class FishCracker extends Food {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(FishCracker.class)
			.t("name", "鱼饼")
			.t("desc", "价格高昂的鱼形饼干，体积虽小却很顶饱。");
	}

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		energy = 200f;
		hornValue = 0;
	}
	@Override public int value() { return 1000 * quantity; }
}
