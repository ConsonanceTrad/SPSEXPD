/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.items.Item;
import pd.items.equipment.weapon.guns.GunD;
import pd.sprites.DemonRabbitSprite;

/** Original SPS-PD runtime and save identity for the demon-blood shooter. */
public class DemonRabbit extends SpsHallsMobs.DemonRabbit {

	{
		spriteClass = DemonRabbitSprite.class;
		properties.add(Property.ORC);
	}

	@Override
	public Item SupercreateLoot() {
		return new GunD();
	}

	public static Class<?> specialLootType() {
		return GunD.class;
	}
}
