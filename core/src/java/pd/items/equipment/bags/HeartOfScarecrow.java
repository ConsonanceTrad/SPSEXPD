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

	//SPSEXPD: 容量 34 = 35-1。窗口里包裹本体自身还占一格，两者相加须正好占满整数行
	//（5 列 x 7 行 = 7 列 x 5 行 = 35），否则装满时会多出一行空行。曾为 35
	@Override public int capacity() { return 34; }
	@Override public int value() { return 50 * quantity; }
}
