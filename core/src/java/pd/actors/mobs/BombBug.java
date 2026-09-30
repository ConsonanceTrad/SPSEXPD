/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.sprites.BombBugSprite;

/** Original SPS runtime/save identity for the fully migrated stone bug. */
public class BombBug extends SpsExitMobs.GuardBombBug {
	{
		spriteClass = BombBugSprite.class;
		properties.remove(Property.ICY);
		properties.add(Property.BEAST);
	}
}
