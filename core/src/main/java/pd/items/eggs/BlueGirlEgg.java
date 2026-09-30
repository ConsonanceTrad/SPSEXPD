/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs;

import pd.actors.mobs.pets.BlueGirl;
import pd.actors.mobs.pets.LegacyPet;
import pd.sprites.ItemSpriteSheet;

public class BlueGirlEgg extends Egg {
	{ image = ItemSpriteSheet.BLUE_GIRL_EGG; poisons = 30; lights = 66; }
	@Override protected LegacyPet hatchling() { return new BlueGirl(); }
	@Override public int value() { return 500 * quantity; }
}
