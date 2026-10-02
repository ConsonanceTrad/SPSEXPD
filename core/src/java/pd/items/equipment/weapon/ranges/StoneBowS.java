package pd.items.equipment.weapon.ranges;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
public class StoneBowS extends RangeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(StoneBowS.class)
			.t("name", "轻巧的绑石弓")
			.t("desc", "将化石绑在弓背上的木弓，但是比较轻巧。");
	}


 public StoneBowS() { super(2, Variant.LIGHT, SpecificPlaceHolderDict.SOMETHING_0); } }
