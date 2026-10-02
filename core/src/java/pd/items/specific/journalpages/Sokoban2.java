/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.specific.journalpages;

import pd.messages.InlineText;
import pd.atlas.items.SpecificTaskDict;

public class Sokoban2 extends JournalPage {
	{
		image = SpecificTaskDict.TELEPORT_COORDINATE;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Sokoban2.class)
			.t("name", "Otiluck的旅行日志之奇异城堡")
			.t("desc", "奇异城堡的地址。\n\n撬开知情人士的嘴花了我不少时间，最后还是让我确定了这个地点。一个看上去像是废弃监狱的地方。巨大的箱子和致命的陷阱胡乱的摆放在哪儿，宝箱在那些东西中间若隐若现。\n\n经过一段时间的分析，我确定这是个谜题，并且可以尝试解开。");
	}



	public Sokoban2() { super(2); }
}
