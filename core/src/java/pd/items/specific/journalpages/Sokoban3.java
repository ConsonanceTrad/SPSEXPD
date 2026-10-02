/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.specific.journalpages;

import pd.messages.InlineText;
import pd.atlas.items.SpecificTaskDict;

public class Sokoban3 extends JournalPage {
	{
		image = SpecificTaskDict.TELEPORT_COORDINATE;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Sokoban3.class)
			.t("name", "Otiluck的旅行日志之传送迷阵")
			.t("desc", "传送迷阵的地址。\n\n那个坐标指引我到了一块荒凉的地方。撒上驱魔粉尘后，一座城堡拔地而起。\n\n进入城堡后，我确认这里和之前废弃监狱一样出自同一个人之手。类似的箱子，类似的陷阱，还有类似的...等一下，一个幻影巨人，看样子它并不欢迎我。\n\n无论如何我也得要继续下去，因为我知道这里肯定有什么巨大的秘密。");
	}



	public Sokoban3() { super(3); }
}
