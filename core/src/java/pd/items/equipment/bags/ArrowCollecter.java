/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.bags;

import pd.atlas.items.EquipmentBagsDict;

import pd.items.Item;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import pd.items.equipment.weapon.ranges.RangeWeapon;
import pd.messages.InlineText;

/** SPS-PD's thirty-slot container for ranged and thrown weapons. */
public class ArrowCollecter extends Bag {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ArrowCollecter.class)
			.t("name", "暗器袋")
			.t("desc", "这个实验背包有三十格空间，投掷武器（暗器）与远程武器统一收纳于此。");
	}




	{
		image = EquipmentBagsDict.SPS_ARROW_COLLECTER;
	}

	@Override
	public boolean canHold(Item item) {
		return (item instanceof RangeWeapon || item instanceof MissileWeapon)
				&& super.canHold(item);
	}

	/** SPSEXPD: 标签页固定排序位。 */
	@Override public int bagOrder() { return 6; }

	//SPSEXPD: 容量 34 = 35-1。窗口里包裹本体自身还占一格，两者相加须正好占满整数行
	//（5 列 x 7 行 = 7 列 x 5 行 = 35），否则装满时会多出一行空行。曾为 35
	@Override public int capacity() { return 34; }
	@Override public int value() { return 50 * quantity; }
}
