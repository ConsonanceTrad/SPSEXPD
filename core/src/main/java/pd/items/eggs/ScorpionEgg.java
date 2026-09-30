/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs;

import pd.actors.mobs.pets.LegacyPet;
import pd.actors.mobs.pets.Scorpion;
import pd.sprites.ItemSpriteSheet;

public class ScorpionEgg extends Egg {
	{ image = ItemSpriteSheet.SCORPION_EGG; moves = 2000; }
	@Override protected LegacyPet hatchling() { return new Scorpion(); }
	@Override public int value() { return 500 * quantity; }
}
