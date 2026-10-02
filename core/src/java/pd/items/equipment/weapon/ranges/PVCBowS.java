package pd.items.equipment.weapon.ranges;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
public class PVCBowS extends RangeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PVCBowS.class)
			.t("name", "轻巧的复合纤维弩")
			.t("desc", "经过复杂工序加工而成的弩，但是比较轻巧。");
	}


 public PVCBowS() { super(5, Variant.LIGHT, SpecificPlaceHolderDict.SOMETHING_0); } }
