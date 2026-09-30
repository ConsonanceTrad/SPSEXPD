/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs;

import pd.actors.mobs.pets.Abi;
import pd.actors.mobs.pets.LegacyPet;
import pd.sprites.ItemSpriteSheet;

/** Alfred's whistle, represented by an egg action in the original pet system. */
public class AflyEgg extends Egg {
	{ image = ItemSpriteSheet.AFLY_EGG; }
	@Override protected LegacyPet hatchling() { return new Abi(); }
	@Override public int value() { return 500 * quantity; }
}
