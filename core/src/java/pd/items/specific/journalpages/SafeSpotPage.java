/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.specific.journalpages;

import pd.Statistics;
import pd.messages.Messages;
import pd.messages.InlineText;
import pd.atlas.items.SpecificTaskDict;

/** The housing contract generated when Otiluke's journal is first acquired. */
public class SafeSpotPage extends JournalPage {
	{
		image = SpecificTaskDict.TELEPORT_COORDINATE;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(SafeSpotPage.class)
			.t("name", "房契")
			.t("grassroom_desc", "森林小屋的地址。")
			.t("forestroom_desc", "荒废草场的地址。")
			.t("cityroom_desc", "城市公寓的地址。");
	}



	public SafeSpotPage() {
		super(0);
	}

	@Override
	public String desc() {
		switch (Statistics.roomType) {
			case 0: return Messages.get(this, "grassroom_desc");
			case 1: return Messages.get(this, "forestroom_desc");
			default: return Messages.get(this, "cityroom_desc");
		}
	}
}
