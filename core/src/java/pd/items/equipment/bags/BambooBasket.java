/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.bags;

import pd.atlas.items.EquipmentBagsDict;

import pd.items.Item;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import pd.messages.InlineText;

/**
 * SPSEXPD: 竹背篓——专门用来堆放投掷果实与大型果实的包裹。
 */
public class BambooBasket extends Bag {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(BambooBasket.class)
			.t("name", "竹背篓")
			.t("desc", "一只用竹子编成的背篓，专门用来堆放投掷果实与大型果实。")
			.t("discover_hint", "你可以在旅途中找到它。");
	}

	{
		//SPSEXPD: 贴图待指认，暂用绒布袋的图标
		image = EquipmentBagsDict.POUCH;
	}

	@Override
	public boolean canHold(Item item) {
		//SPSEXPD: 只收纳投掷果实（含大型果实）
		if (item instanceof MissileWeapon && ((MissileWeapon) item).isFruit()) {
			return super.canHold(item);
		}
		return false;
	}

	/** SPSEXPD: 标签页固定排序位。 */
	@Override public int bagOrder() { return 4; }

	@Override

	public int capacity() {
		return 34;
	}

	@Override
	public int value() {
		return 30;
	}
}
