/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.items.ExpOre;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.sprites.LevelCheckerSprite;

/** Original SPS-PD runtime and save identity for the adjudicator. */
public class LevelChecker extends SpsCityMobs.LevelChecker {

	{
		spriteClass = LevelCheckerSprite.class;
	}

	@Override
	public Item SupercreateLoot() {
		return new ExpOre();
	}

	public static Class<?> specialLootType() {
		return ExpOre.class;
	}
}
