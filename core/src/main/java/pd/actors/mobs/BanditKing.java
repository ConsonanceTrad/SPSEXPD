/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.sprites.BanditKingSprite;

/** Original SPS-PD runtime and save identity for the life bandit. */
public class BanditKing extends SpsPrisonMobs.BanditKing {

	{
		spriteClass = BanditKingSprite.class;
		properties.add(Property.ELF);
		if (SpsPrisonMobs.BanditKing.grantsSpork()) Dungeon.sporkAvailable = false;
	}
}
