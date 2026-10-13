/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.bags;

import pd.atlas.items.EquipmentBagsDict;

import pd.items.Item;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import pd.messages.InlineText;

/** SPS-PD's thirty-slot container for ranged and thrown weapons. */
public class ArrowCollecter extends Bag {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ArrowCollecter.class)
			.t("name", "暗器袋")
			.t("desc", "这具窄长的容器由四根细鳞皮带紧缚在小臂内侧，内侧缝着几个薄隔层与牵引绳，屈伸时也不会影响行动。\n\n只需要勾下手指，存储在其中的暗器就会瞬间滑入手掌中。\n\n由于精心的保护，其中的投掷武器会比寻常更耐用一些。");
	}




	{
		image = EquipmentBagsDict.SPS_ARROW_COLLECTER;
	}

	@Override
	public boolean canHold(Item item) {
		//SPSEXPD: 果实是 MissileWeapon 的子类，但按用户要求不装进暗器袋（它们归竹背篓）
		if (item instanceof MissileWeapon && ((MissileWeapon) item).isFruit()) return false;
		//SPSEXPD: 远程武器（RangeWeapon）改归草靶子，不再收进暗器袋
		return item instanceof MissileWeapon
				&& super.canHold(item);
	}

	/** SPSEXPD: 标签页固定排序位。 */
	@Override public int bagOrder() { return 6; }

	//SPSEXPD: 容量 34 = 35-1。窗口里包裹本体自身还占一格，两者相加须正好占满整数行
	//（5 列 x 7 行 = 7 列 x 5 行 = 35），否则装满时会多出一行空行。曾为 35
	@Override public int capacity() { return 34; }
	@Override public int value() { return 50 * quantity; }
}
