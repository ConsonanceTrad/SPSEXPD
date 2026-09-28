/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DemonflowerSprite;

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
