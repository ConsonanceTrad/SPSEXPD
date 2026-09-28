/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.items.SaveYourLife;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RatBossSprite;

/** Original SPS-PD runtime and save identity for the leader rat. */
public class RatBoss extends SpsSewerMobs.RatBoss {

	{
		spriteClass = RatBossSprite.class;
	}

	public static Class<?> specialLootType() {
		return SaveYourLife.class;
	}
}
