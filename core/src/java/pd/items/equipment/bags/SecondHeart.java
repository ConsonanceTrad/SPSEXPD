/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.bags;

import pd.atlas.items.EquipmentBagsDict;
import pd.messages.InlineText;
import pd.messages.Messages;

/** SPSEXPD: 第二心脏——拓展{target}的包裹组件（+15 格）。 */
public class SecondHeart extends BagComponent.BagExpand {

	static {
		InlineText.of(SecondHeart.class)
				.t("name", "第二心脏")
				.t("desc", "稻草人胸口的位置又多了一颗心，草靶子能收的东西更多了。");
	}

	{
		image = EquipmentBagsDict.HEART_OF_SCARECROW_0;
	}

	@Override
	public Class<? extends Bag> targetBag() {
		return pd.items.equipment.bags.HeartOfScarecrow.class;
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