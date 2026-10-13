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
			.t("desc", "这个方形的容器看起来可以装下一整场宴会的食材和餐品，不过你的口粮也刚好能放在里面。\n\n里面甚至有一些能用来固定鸡蛋、香料罐和餐具的小隔间。");
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

	/** SPSEXPD: 标签页固定排序位。 */
	@Override public int bagOrder() { return 3; }

	@Override public int capacity() { return 34; }
	@Override public int value() { return 50 * quantity; }
}
