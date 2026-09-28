/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.food.BugMeat;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SpiderBotSprite;

/** Original SPS-PD runtime and save identity for the scavenger. */
public class SpiderBot extends SpsCityMobs.SpiderBot {

	{
		spriteClass = SpiderBotSprite.class;
	}

	@Override
	public Item SupercreateLoot() {
		return new BugMeat();
	}

	public static Class<?> specialLootType() {
		return BugMeat.class;
	}
}
