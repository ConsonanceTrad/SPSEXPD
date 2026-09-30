/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs;
import pd.actors.mobs.pets.FoxHelper;
import pd.actors.mobs.pets.LegacyPet;
import pd.sprites.ItemSpriteSheet;
public class FoxHelperEgg extends Egg { { image = ItemSpriteSheet.FOX_HELPER_EGG; } @Override protected LegacyPet hatchling() { return new FoxHelper(); } @Override public int value() { return 500 * quantity; } }
