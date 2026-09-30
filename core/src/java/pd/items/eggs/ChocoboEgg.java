/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs;

import pd.actors.mobs.pets.Chocobo;
import pd.actors.mobs.pets.LegacyPet;
import pd.sprites.ItemSpriteSheet;

public class ChocoboEgg extends Egg {
	{ image = ItemSpriteSheet.CHOCOBO_EGG; }
	@Override protected LegacyPet hatchling() { return new Chocobo(); }
	@Override public int value() { return 500 * quantity; }
}
