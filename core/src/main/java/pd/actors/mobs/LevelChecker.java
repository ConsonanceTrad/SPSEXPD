/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.items.ExpOre;
import pd.items.Item;
import pd.sprites.LevelCheckerSprite;

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
