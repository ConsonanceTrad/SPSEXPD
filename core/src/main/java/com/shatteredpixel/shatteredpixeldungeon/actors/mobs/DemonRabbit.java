/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.guns.GunD;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DemonRabbitSprite;

/** Original SPS-PD runtime and save identity for the demon-blood shooter. */
public class DemonRabbit extends SpsHallsMobs.DemonRabbit {

	{
		spriteClass = DemonRabbitSprite.class;
		properties.add(Property.ORC);
	}

	@Override
	public Item SupercreateLoot() {
		return new GunD();
	}

	public static Class<?> specialLootType() {
		return GunD.class;
	}
}
