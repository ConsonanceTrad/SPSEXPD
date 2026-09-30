/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.items.Generator;
import pd.items.Item;
import pd.sprites.BrownBatSprite;

/** Original SPS-PD runtime and save identity for the small sewer bat. */
public class BrownBat extends SpsSewerMobs.BrownBat {

	{
		spriteClass = BrownBatSprite.class;
		properties.add(Property.BEAST);
	}

	public static Generator.Category specialLootCategory() {
		return Generator.Category.SEED4;
	}

	@Override
	public Item SupercreateLoot() {
		return Generator.random(specialLootCategory());
	}
}
