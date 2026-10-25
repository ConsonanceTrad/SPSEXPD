/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.bags;

import pd.atlas.items.EquipmentBagsDict;
import pd.messages.InlineText;
import pd.messages.Messages;

/** SPSEXPD: 药水挂箱——拓展{target}的包裹组件（+15 格）。 */
public class PotionBox extends BagExpand {

	static {
		InlineText.of(PotionBox.class)
				.t("name", "药水挂箱")
				.t("desc", "挂在药水箱下面的小木箱，瓶瓶罐罐各归其位。");
	}

	{
		image = EquipmentBagsDict.BANDOLIER;
	}

	@Override
	public Class<? extends Bag> targetBag() {
		return pd.items.equipment.bags.PotionBandolier.class;
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