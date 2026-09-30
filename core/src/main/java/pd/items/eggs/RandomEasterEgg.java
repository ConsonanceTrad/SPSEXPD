/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs;

import pd.actors.mobs.pets.Bunny;
import pd.actors.mobs.pets.CocoCat;
import pd.actors.mobs.pets.LegacyPet;
import pd.actors.mobs.pets.Velocirooster;
import pd.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class RandomEasterEgg extends Egg {
	{ image = ItemSpriteSheet.COCO_CAT_EGG; }
	@Override protected LegacyPet hatchling() {
		switch (Random.Int(3)) {
			case 0: return new Bunny();
			case 1: return new CocoCat();
			default: return new Velocirooster();
		}
	}
	@Override public int value() { return 500 * quantity; }
}
