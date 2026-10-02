/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.items.Item;
import pd.items.consum.food.BugMeat;
import pd.sprites.SpiderBotSprite;

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
