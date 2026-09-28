/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.items.UnBlessAnkh;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFirebolt;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ZombieSprite;

/** Original SPS-PD runtime and save identity for the infected zombie. */
public class Zombie extends SpsPrisonMobs.Zombie {

	{
		spriteClass = ZombieSprite.class;
		weaknesses.add(Burning.class);
		weaknesses.add(WandOfFirebolt.class);
	}

	public static Class<?> specialLootType() {
		return UnBlessAnkh.class;
	}
}
