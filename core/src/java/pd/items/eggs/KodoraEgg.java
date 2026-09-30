/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs;
import pd.actors.mobs.pets.Kodora;
import pd.actors.mobs.pets.LegacyPet;
import pd.sprites.ItemSpriteSheet;
public class KodoraEgg extends Egg { { image = ItemSpriteSheet.KODORA_EGG; } @Override protected LegacyPet hatchling() { return new Kodora(); } @Override public int value() { return 500 * quantity; } }
