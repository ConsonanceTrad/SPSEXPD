/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.bags;

import pd.atlas.items.EquipmentBagsDict;

import pd.items.Item;
import pd.items.RationGrinder;
import pd.items.consum.brewed.Brewed;
import pd.items.consum.food.BugMeat;
import pd.items.consum.food.Food;
import pd.items.consum.potions.brews.Brew;
import pd.messages.InlineText;
import render.utils.serialize.Bundle;

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

	/** SPSEXPD: 是否已安装干粮碾制机（安装后最后一格变成碾制机）。 */
	public boolean grinderInstalled = false;

	private static final String GRINDER_INSTALLED = "grinder_installed";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( GRINDER_INSTALLED, grinderInstalled );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		grinderInstalled = bundle.getBoolean( GRINDER_INSTALLED );
	}

	/** SPSEXPD: 末格显示的碾制机（临时实例，不占购物车库存，也不入档）。 */
	public Item grinderSlot() {
		if (!grinderInstalled) return null;
		return RationGrinder.installedOn(this);
	}

	/** SPSEXPD: 标签页固定排序位。 */
	@Override public int bagOrder() { return 3; }

	@Override public int capacity() { return 34; }
	@Override public int value() { return 50 * quantity; }
}
