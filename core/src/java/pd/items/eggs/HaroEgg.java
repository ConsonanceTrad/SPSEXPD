/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs;
import pd.actors.mobs.pets.Haro;
import pd.actors.mobs.pets.LegacyPet;
import pd.sprites.ItemSpriteSheet;
public class HaroEgg extends Egg {
	{ image = ItemSpriteSheet.HARO_EGG; }
	@Override protected LegacyPet hatchling() { return new Haro(); }
	@Override public int value() { return 500 * quantity; }
}
