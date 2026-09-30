/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.actors.buffs.Burning;
import pd.items.UnBlessAnkh;
import pd.items.wands.WandOfFirebolt;
import pd.sprites.ZombieSprite;

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
