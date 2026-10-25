/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.bags;

import pd.atlas.items.EquipmentBagsDict;
import pd.messages.InlineText;
import pd.messages.Messages;

/** SPSEXPD: 空间拓环——拓展{target}的包裹组件（+15 格）。 */
public class SpaceRing extends BagExpand {

	static {
		InlineText.of(SpaceRing.class)
				.t("name", "空间拓环")
				.t("desc", "套在魔法套筒口上的金属环，把里面的空间又撑开一层。");
	}

	{
		image = EquipmentBagsDict.HOLSTER;
	}

	@Override
	public Class<? extends Bag> targetBag() {
		return pd.items.equipment.bags.MagicalHolster.class;
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