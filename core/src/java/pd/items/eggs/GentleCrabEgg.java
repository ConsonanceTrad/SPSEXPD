/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs;
import pd.actors.mobs.pets.GentleCrab;
import pd.actors.mobs.pets.LegacyPet;
import pd.sprites.ItemSpriteSheet;
public class GentleCrabEgg extends Egg { { image = ItemSpriteSheet.GENTLE_CRAB_EGG; } @Override protected LegacyPet hatchling() { return new GentleCrab(); } @Override public int value() { return 500 * quantity; } }
