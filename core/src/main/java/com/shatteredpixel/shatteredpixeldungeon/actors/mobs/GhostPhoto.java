/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.sprites.LivePhotoSprite;

/** Original SPS-PD runtime and save identity for the mouldy painting. */
public class GhostPhoto extends SpsPrisonMobs.GhostPhoto {

	{
		spriteClass = LivePhotoSprite.class;
		properties.add(Property.UNKNOW);
	}

	@Override
	public Item SupercreateLoot() {
		return Generator.random(Generator.Category.SEED);
	}

	public static Generator.Category specialLootCategory() {
		return Generator.Category.SEED;
	}
}
