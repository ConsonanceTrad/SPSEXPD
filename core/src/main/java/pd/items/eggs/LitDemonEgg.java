/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs;
import pd.actors.mobs.pets.LegacyPet;
import pd.actors.mobs.pets.LitDemon;
import pd.sprites.ItemSpriteSheet;
public class LitDemonEgg extends Egg { { image = ItemSpriteSheet.LIT_DEMON_EGG; } @Override protected LegacyPet hatchling() { return new LitDemon(); } @Override public int value() { return 500 * quantity; } }
