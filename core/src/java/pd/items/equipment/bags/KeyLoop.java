/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.bags;

import pd.atlas.items.EquipmentBagsDict;
import pd.messages.InlineText;
import pd.messages.Messages;

/** SPSEXPD: 钥匙挂环——拓展{target}的包裹组件（+15 格）。 */
public class KeyLoop extends BagExpand {

	static {
		InlineText.of(KeyLoop.class)
				.t("name", "钥匙挂环")
				.t("desc", "多接在钥匙环上的一个铜圈，钥匙再多也挂得下。");
	}

	{
		image = EquipmentBagsDict.SPS_KEY_RING;
	}

	@Override
	public Class<? extends Bag> targetBag() {
		return pd.items.equipment.bags.KeyRing.class;
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