/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.bags;

import pd.atlas.items.EquipmentBagsDict;
import pd.messages.InlineText;
import pd.messages.Messages;

/** SPSEXPD: 暗器备匣——拓展{target}的包裹组件（+15 格）。 */
public class AmmoCase extends BagExpand {

	static {
		InlineText.of(AmmoCase.class)
				.t("name", "暗器备匣")
				.t("desc", "别在暗器袋侧面的窄匣，备用的镖与针放在这里。");
	}

	{
		image = EquipmentBagsDict.SPS_ARROW_COLLECTER;
	}

	@Override
	public Class<? extends Bag> targetBag() {
		return pd.items.equipment.bags.ArrowCollecter.class;
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