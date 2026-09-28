/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BrownBatSprite;

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
