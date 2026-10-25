/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.bags;

import pd.atlas.items.EquipmentBagsDict;
import pd.messages.InlineText;
import pd.messages.Messages;

/** SPSEXPD: 储物宝石——拓展{target}的包裹组件（+15 格）。 */
public class StorageGem extends BagExpand {

	static {
		InlineText.of(StorageGem.class)
				.t("name", "储物宝石")
				.t("desc", "一颗内部能折叠空间的宝石。把它放在背包里，背起来会轻一点，装得也多一点。");
	}

	{
		image = EquipmentBagsDict.POUCH;
	}

	@Override
	public Class<? extends Bag> targetBag() {
		return pd.actors.hero.Belongings.Backpack.class;
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