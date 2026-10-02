/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.items.Item;
import pd.items.equipment.weapon.guns.ToyGun;
import pd.sprites.MusketeerSprite;

/** Original SPS-PD runtime and save identity for the dwarf musketeer. */
public class Musketeer extends SpsCityMobs.Musketeer {

	{
		spriteClass = MusketeerSprite.class;
	}

	@Override
	public Item SupercreateLoot() {
		return new ToyGun();
	}

	public static Class<?> specialLootType() {
		return ToyGun.class;
	}
}
