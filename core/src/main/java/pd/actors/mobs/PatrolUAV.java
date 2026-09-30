/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.actors.blobs.effectblobs.ElectriShock;
import pd.items.Item;
import pd.items.scrolls.ScrollOfRecharging;
import pd.items.wands.WandOfLightning;
import pd.items.wands.WandOfTCloud;
import pd.sprites.PatrolUAVSprite;
import render.utils.Random;

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
