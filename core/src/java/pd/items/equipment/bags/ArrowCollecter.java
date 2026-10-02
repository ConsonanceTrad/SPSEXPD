/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.bags;

import pd.atlas.items.EquipmentBagsDict;

import pd.items.Item;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import pd.items.equipment.weapon.ranges.RangeWeapon;

/** SPS-PD's thirty-slot container for ranged and thrown weapons. */
public class ArrowCollecter extends Bag {

	{
		image = EquipmentBagsDict.SPS_ARROW_COLLECTER;
	}

	@Override
	public boolean canHold(Item item) {
		return (item instanceof RangeWeapon || item instanceof MissileWeapon)
				&& super.canHold(item);
	}

	@Override public int capacity() { return 34; }
	@Override public int value() { return 50 * quantity; }
}
