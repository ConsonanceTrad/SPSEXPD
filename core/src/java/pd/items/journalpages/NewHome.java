/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.journalpages;

import pd.sprites.ItemSpriteSheet;

/** The original sample-house location page, which unlocks adventure destination 8. */
public class NewHome extends JournalPage {
	public NewHome() {
		super(8);
		image = ItemSpriteSheet.SPS_JOURNAL_PAGE;
	}
}
