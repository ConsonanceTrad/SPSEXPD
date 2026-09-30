/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs;
import pd.actors.mobs.pets.FrogPet;
import pd.actors.mobs.pets.LegacyPet;
import pd.sprites.ItemSpriteSheet;
public class FrogpetEgg extends Egg { { image = ItemSpriteSheet.FROG_PET_EGG; } @Override protected LegacyPet hatchling() { return new FrogPet(); } @Override public int value() { return 500 * quantity; } }
