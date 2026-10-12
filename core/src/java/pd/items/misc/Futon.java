/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.Item;
import pd.messages.InlineText;

/**
 * SPSEXPD: 蒲团 —— 信徒开局携带的占位道具，暂无任何效果。
 */
public class Futon extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Futon.class)
			.t("name", "蒲团")
			.t("desc", "一只供人盘坐的圆垫，编得松散而柔软。目前它只是一件随身纪念物，还没有任何实际效果。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.SOMETHING_0;
	}
}
