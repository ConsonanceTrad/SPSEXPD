/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.journalpages;

import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

/** The original sample-house location page, which unlocks adventure destination 8. */
public class NewHome extends JournalPage {
	public NewHome() {
		super(8);
		image = ItemSpriteSheet.SPS_JOURNAL_PAGE;
	}
}
