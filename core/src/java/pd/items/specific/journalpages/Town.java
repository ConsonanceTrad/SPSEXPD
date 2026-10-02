/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.specific.journalpages;

import pd.messages.InlineText;

public class Town extends JournalPage {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Town.class)
			.t("name", "多利亚小镇")
			.t("desc", "多利亚小镇的地址。\n\n_我们非常抱歉：目前我们已经不再生产及销售多利亚石板。但是如果你不相信的话，欢迎来我们小镇看看。_");
	}

	public Town() { super(5); }
}
