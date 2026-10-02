/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.items.Item;
import pd.items.equipment.artifacts.TimekeepersHourglass;
import pd.items.consum.medicine.Timepill2;
import pd.items.consum.potions.PotionOfMindVision;
import pd.sprites.TimeKeeperSprite;
import render.utils.math.Random;

/** Original SPS-PD runtime and save identity for the timekeeper. */
public class TimeKeeper extends SpsCaveMobs.TimeKeeper {

	{
		spriteClass = TimeKeeperSprite.class;
		properties.add(Property.UNKNOW);
		properties.add(Property.MAGICER);
	}

	@Override
	public Item SupercreateLoot() {
		return Random.oneOf(new Timepill2(), new PotionOfMindVision(), new TimekeepersHourglass());
	}

	public static Class<?>[] specialLootTypes() {
		return new Class<?>[]{Timepill2.class, PotionOfMindVision.class, TimekeepersHourglass.class};
	}
}
