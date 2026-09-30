/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs;
import pd.actors.mobs.pets.CocoCat;
import pd.actors.mobs.pets.LegacyPet;
import pd.sprites.ItemSpriteSheet;
public class CocoCatEgg extends Egg {
	{ image = ItemSpriteSheet.COCO_CAT_EGG; }
	@Override protected LegacyPet hatchling() { return new CocoCat(); }
	@Override public int value() { return 500 * quantity; }
}
