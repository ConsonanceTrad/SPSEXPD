/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.meleethrow;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
import pd.atlas.items.ConsumThrowsDict;


public class HugeShuriken extends MeleeThrowWeapon {
	{
		image = ConsumThrowsDict.GIANT_SHURIKEN;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(HugeShuriken.class)
			.t("name", "巨型手里剑")
			.t("desc", "锋锐的巨大星形金属刃片既能近战，也能投掷并回收。——Dachhack");
	}



	public HugeShuriken() { super(4, 38, 52, SpecificPlaceHolderDict.SOMETHING_0); }
}
