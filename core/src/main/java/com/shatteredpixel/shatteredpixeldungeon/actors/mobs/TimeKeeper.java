/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.medicine.Timepill2;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMindVision;
import com.shatteredpixel.shatteredpixeldungeon.sprites.TimeKeeperSprite;
import com.watabou.utils.Random;

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
