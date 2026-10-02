/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.specific.journalpages;

import pd.messages.InlineText;

public class Sokoban1 extends JournalPage {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Sokoban1.class)
			.t("name", "Otiluck的旅行日志之推箱教程")
			.t("desc", "推箱教程的地址。\n\n说起我的故乡，多利亚小镇，想必大家不会陌生。优美的环境，舒适的气候，丰富的矿产资源，一度让其成为最适宜居住的城镇之一。当然那已经是我成年前的事了。\n\n肆意开采矿产外加乱排乱放垃圾使得小镇一天天衰败。我想我应该去做些什么。");
	}



	public Sokoban1() { super(1); }
}
