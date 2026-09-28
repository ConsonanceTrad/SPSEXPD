/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.guns.ToyGun;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MusketeerSprite;

/** Original SPS-PD runtime and save identity for the dwarf musketeer. */
public class Musketeer extends SpsCityMobs.Musketeer {

	{
		spriteClass = MusketeerSprite.class;
	}

	@Override
	public Item SupercreateLoot() {
		return new ToyGun();
	}

	public static Class<?> specialLootType() {
		return ToyGun.class;
	}
}
