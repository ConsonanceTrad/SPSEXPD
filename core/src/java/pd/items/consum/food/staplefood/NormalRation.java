/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.staplefood;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;


public class NormalRation extends StapleFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(NormalRation.class)
			.t("name", "干粮包")
			.t("desc", "里面没什么稀奇的：肉干、饼干，以及类似的旅行食物。");
	}



	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		energy = 300f;
	}
	@Override public int value() { return 5 * quantity; }
}
