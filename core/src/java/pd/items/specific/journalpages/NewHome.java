/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.specific.journalpages;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
import pd.atlas.items.SpecificTaskDict;


/** The original sample-house location page, which unlocks adventure destination 8. */
public class NewHome extends JournalPage {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(NewHome.class)
			.t("name", "样板房坐标")
			.t("desc", "推荐房源，现场看房。");
	}



	public NewHome() {
		super(8);
		image = SpecificTaskDict.TELEPORT_COORDINATE;
	}
}
