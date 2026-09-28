/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.items.quest.ChallengeJournal;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

/** REN's physical challenge book, obtained in Dolya town. */
public class ChallengeBook extends ChallengeJournal {

	{
		image = ItemSpriteSheet.CHALLENGE_BOOK;
		stackable = true;
	}
}
