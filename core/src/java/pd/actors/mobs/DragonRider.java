/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.items.Item;
import pd.items.consum.eggs.randomone.RandomMonthEgg;
import pd.sprites.DragonRiderSprite;

/** Original SPS-PD runtime and save identity for the dragon rider. */
public class DragonRider extends SpsCityMobs.DragonRider {

	{
		spriteClass = DragonRiderSprite.class;
	}

	@Override
	public Item SupercreateLoot() {
		return new RandomMonthEgg();
	}

	public static Class<?> specialLootType() {
		return RandomMonthEgg.class;
	}
}
