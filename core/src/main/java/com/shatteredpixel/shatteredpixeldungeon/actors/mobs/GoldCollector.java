/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.MasterThievesArmband;
import com.shatteredpixel.shatteredpixeldungeon.items.sellitem.VIPcard;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GoldCollectorSprite;
import com.watabou.utils.Random;

/** Original SPS-PD runtime and save identity for the goblin tax collector. */
public class GoldCollector extends SpsPrisonMobs.GoldCollector {

	{
		spriteClass = GoldCollectorSprite.class;
		properties.add(Property.GOBLIN);
	}

	@Override
	public Item SupercreateLoot() {
		return Random.oneOf(new Gold(100), new VIPcard(), new MasterThievesArmband());
	}

	public static Class<?>[] specialLootTypes() {
		return new Class<?>[]{Gold.class, VIPcard.class, MasterThievesArmband.class};
	}
}
