package pd.items.equipment.weapon.ranges;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
public class PVCBowN extends RangeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PVCBowN.class)
			.t("name", "普通的复合纤维弩")
			.t("desc", "经过复杂工序加工而成的弩，可以发射箭矢。");
	}
 public PVCBowN() { super(5, Variant.NORMAL, SpecificPlaceHolderDict.SOMETHING_0); } }
