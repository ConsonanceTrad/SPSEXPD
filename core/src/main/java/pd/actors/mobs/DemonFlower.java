/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.items.Generator;
import pd.items.Item;
import pd.sprites.DemonflowerSprite;

/** Original SPS-PD runtime and save identity for the infernal flower. */
public class DemonFlower extends SpsHallsMobs.DemonFlower {

	{
		spriteClass = DemonflowerSprite.class;
		properties.add(Property.PLANT);
	}

	@Override
	public Item SupercreateLoot() {
		return Generator.random(Generator.Category.PILL);
	}

	public static Generator.Category specialLootCategory() {
		return Generator.Category.PILL;
	}
}
