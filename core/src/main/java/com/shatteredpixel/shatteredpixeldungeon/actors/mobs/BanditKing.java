/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BanditKingSprite;

/** Original SPS-PD runtime and save identity for the life bandit. */
public class BanditKing extends SpsPrisonMobs.BanditKing {

	{
		spriteClass = BanditKingSprite.class;
		properties.add(Property.ELF);
		if (SpsPrisonMobs.BanditKing.grantsSpork()) Dungeon.sporkAvailable = false;
	}
}
