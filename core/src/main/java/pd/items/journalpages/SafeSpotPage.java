/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.journalpages;

import pd.Statistics;
import pd.messages.Messages;

/** The housing contract generated when Otiluke's journal is first acquired. */
public class SafeSpotPage extends JournalPage {
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
