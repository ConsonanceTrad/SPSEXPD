/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.bags;

import pd.atlas.items.EquipmentBagsDict;
import pd.messages.InlineText;
import pd.messages.Messages;

/** SPSEXPD: 竹制小框——拓展{target}的包裹组件（+15 格）。 */
public class BambooFrame extends BagComponent.BagExpand {

	static {
		InlineText.of(BambooFrame.class)
				.t("name", "竹制小框")
				.t("desc", "卡在竹背篓内侧的竹框，把篓子又分出一层。");
	}

	{
		image = EquipmentBagsDict.BAMBOO_BASKET;
	}

	@Override
	public Class<? extends Bag> targetBag() {
		return pd.items.equipment.bags.BambooBasket.class;
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