/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.bags;

import pd.atlas.items.EquipmentBagsDict;

import pd.items.Item;
import pd.items.ShadowEaterKey;
import pd.items.equipment.armor.Armor;
import pd.items.equipment.weapon.melee.MeleeWeapon;

/** The original portable training target, used as a thirty-slot equipment bag. */
public class HeartOfScarecrow extends Bag {

	{
		image = EquipmentBagsDict.HEART_OF_SCARECROW_0;
	}

	@Override
	public boolean canHold(Item item) {
		if (item instanceof MeleeWeapon || item instanceof Armor || item instanceof ShadowEaterKey) {
			return super.canHold(item);
		}
		return false;
	}

	@Override public int capacity() { return 34; }
	@Override public int value() { return 50 * quantity; }
}
