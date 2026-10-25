/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.bags;

import pd.atlas.items.EquipmentBagsDict;
import pd.messages.InlineText;
import pd.messages.Messages;

/** SPSEXPD: 绒布小包——拓展{target}的包裹组件（+15 格）。 */
public class PouchExpand extends BagExpand {

	static {
		InlineText.of(PouchExpand.class)
				.t("name", "绒布小包")
				.t("desc", "缝在绒布包内侧的小夹层，装药剂之外的小东西正好。");
	}

	{
		image = EquipmentBagsDict.POUCH;
	}

	@Override
	public Class<? extends Bag> targetBag() {
		return pd.items.equipment.bags.VelvetPouch.class;
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