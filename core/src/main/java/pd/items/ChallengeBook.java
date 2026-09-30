/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.items.quest.ChallengeJournal;
import pd.sprites.ItemSpriteSheet;

/** REN's physical challenge book, obtained in Dolya town. */
public class ChallengeBook extends ChallengeJournal {

	{
		image = ItemSpriteSheet.CHALLENGE_BOOK;
		stackable = true;
	}
}
