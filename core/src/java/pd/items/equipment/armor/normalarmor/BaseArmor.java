/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.armor.normalarmor;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;


/** Cosmetic armor used by the demon-contract warrior start. */
public class BaseArmor extends NormalArmor {
	{
		image = SpecificPlaceHolderDict.SPS_PH_ARMOR_SPARE;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(BaseArmor.class)
			.t("name", "基础护甲")
			.t("desc", "这并不是一件护甲，但是为了美观，这件物品被装备在该角色身上。\n非护甲");
	}



	public BaseArmor() {
		// Legacy upgrades cancelled their own DR changes, so this remains 0-0 at every level.
		super(0, 1f, 1f, 4, 0, 0, 0, 0, 0, SpecificPlaceHolderDict.SOMETHING_0);
	}
}
