/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.effectblobs.ElectriShock;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLightning;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfTCloud;
import com.shatteredpixel.shatteredpixeldungeon.sprites.PatrolUAVSprite;
import com.watabou.utils.Random;

/** Original SPS-PD runtime and save identity for the patrol drone. */
public class PatrolUAV extends SpsSewerMobs.PatrolUAV {

	{
		spriteClass = PatrolUAVSprite.class;
		properties.remove(Property.INORGANIC);
		properties.add(Property.MECH);
		immunities.add(ElectriShock.class);
		immunities.add(WandOfLightning.class);
	}

	@Override
	public Item SupercreateLoot() {
		return Random.oneOf(new ScrollOfRecharging(), new WandOfTCloud());
	}

	public static Class<?>[] specialLootTypes() {
		return new Class<?>[]{ScrollOfRecharging.class, WandOfTCloud.class};
	}
}
