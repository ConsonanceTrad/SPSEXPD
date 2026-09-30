/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs;
import pd.actors.mobs.pets.LegacyPet;
import pd.actors.mobs.pets.Velocirooster;
import pd.sprites.ItemSpriteSheet;
public class VelociroosterEgg extends Egg {
	{ image = ItemSpriteSheet.VELOCIROOSTER_EGG; }
	@Override protected LegacyPet hatchling() { return new Velocirooster(); }
	@Override public int value() { return 500 * quantity; }
}
