/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.bags;

import pd.atlas.items.EquipmentBagsDict;
import pd.messages.InlineText;
import pd.messages.Messages;

/** SPSEXPD: 卷轴捆袋——拓展{target}的包裹组件（+15 格）。 */
public class ScrollBundle extends BagExpand {

	static {
		InlineText.of(ScrollBundle.class)
				.t("name", "卷轴捆袋")
				.t("desc", "捆在卷轴筒外侧的皮革袋，能多塞几卷。");
	}

	{
		image = EquipmentBagsDict.HOLDER;
	}

	@Override
	public Class<? extends Bag> targetBag() {
		return pd.items.equipment.bags.ScrollHolder.class;
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