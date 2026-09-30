/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.items.Generator;
import pd.items.Item;
import pd.sprites.LiveMossSprite;
import com.watabou.utils.Random;

/** Original SPS-PD runtime and save identity for living moss. */
public class LiveMoss extends SpsSewerMobs.LiveMoss {

	{
		spriteClass = LiveMossSprite.class;
		loot = Generator.Category.MUSHROOM;
		properties.add(Property.PLANT);
	}

	@Override
	public Item SupercreateLoot() {
		return Random.oneOf(Generator.random(Generator.Category.POTION),
				Generator.random(Generator.Category.SUMMONED));
	}

	public static Generator.Category[] specialLootCategories() {
		return new Generator.Category[]{Generator.Category.POTION, Generator.Category.SUMMONED};
	}
}
