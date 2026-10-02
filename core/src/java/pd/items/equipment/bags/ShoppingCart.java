/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.bags;

import pd.atlas.items.EquipmentBagsDict;

import pd.items.Item;
import pd.items.consum.brewed.Brewed;
import pd.items.consum.food.BugMeat;
import pd.items.consum.food.Food;
import pd.items.consum.potions.brews.Brew;
import pd.messages.InlineText;

/** The thirty-slot SPS food and brew container. */
public class ShoppingCart extends Bag {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ShoppingCart.class)
			.t("name", "购物车")
			.t("desc", "容量很大的购物车，可以收纳食物和酿造药剂，但不能装入虫肉。");
	}




	{
		image = EquipmentBagsDict.SHOPPING_CART_0;
	}

	@Override
	public boolean canHold(Item item) {
		if ((item instanceof Food || item instanceof Brew || item instanceof Brewed) && !(item instanceof BugMeat)) {
			return super.canHold(item);
		}
		return false;
	}

	@Override public int capacity() { return 34; }
	@Override public int value() { return 50 * quantity; }
}
