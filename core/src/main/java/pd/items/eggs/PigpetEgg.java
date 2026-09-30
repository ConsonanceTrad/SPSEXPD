/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs;
import pd.actors.mobs.pets.LegacyPet;
import pd.actors.mobs.pets.PigPet;
import pd.sprites.ItemSpriteSheet;
public class PigpetEgg extends Egg {
	{ image = ItemSpriteSheet.PIG_PET_EGG; }
	@Override protected LegacyPet hatchling() { return new PigPet(); }
	@Override public int value() { return 500 * quantity; }
}
