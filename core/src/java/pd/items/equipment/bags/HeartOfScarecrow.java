/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.bags;

import pd.atlas.items.EquipmentBagsDict;

import pd.items.Item;
import pd.items.ShadowEaterKey;
import pd.items.equipment.armor.Armor;
import pd.items.equipment.weapon.melee.MeleeWeapon;
import pd.messages.InlineText;

/** The original portable training target, used as a thirty-slot equipment bag. */
public class HeartOfScarecrow extends Bag {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(HeartOfScarecrow.class)
			.t("name", "草靶子")
			.t("desc", "可以收纳武器、护甲与暗噬原型的便携训练靶。");
	}




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

	/** SPSEXPD: 标签页固定排序位。 */
	@Override public int bagOrder() { return 7; }

	@Override public int capacity() { return 35; }
	@Override public int value() { return 50 * quantity; }
}
