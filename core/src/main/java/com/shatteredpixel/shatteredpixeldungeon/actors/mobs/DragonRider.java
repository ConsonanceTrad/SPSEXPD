/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.randomone.RandomMonthEgg;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DragonRiderSprite;

/** Original SPS-PD runtime and save identity for the dragon rider. */
public class DragonRider extends SpsCityMobs.DragonRider {

	{
		spriteClass = DragonRiderSprite.class;
	}

	@Override
	public Item SupercreateLoot() {
		return new RandomMonthEgg();
	}

	public static Class<?> specialLootType() {
		return RandomMonthEgg.class;
	}
}
