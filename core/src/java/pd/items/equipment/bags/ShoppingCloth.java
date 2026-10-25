/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.bags;

import pd.atlas.items.EquipmentBagsDict;
import pd.messages.InlineText;
import pd.messages.Messages;

/** SPSEXPD: 购物布袋——拓展{target}的包裹组件（+15 格）。 */
public class ShoppingCloth extends BagComponent.BagExpand {

	static {
		InlineText.of(ShoppingCloth.class)
				.t("name", "购物布袋")
				.t("desc", "挂在购物车把手上的布袋，随手买的东西先扔这里。");
	}

	{
		image = EquipmentBagsDict.SHOPPING_CART_0;
	}

	@Override
	public Class<? extends Bag> targetBag() {
		return pd.items.equipment.bags.ShoppingCart.class;
	}

	@Override
	public String name() {
		return Messages.get(this, "name");
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc") + "\n\n" + bonusSuffix();
	}
}