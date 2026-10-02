package pd.items.equipment.weapon.ranges;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
public class StoneBowN extends RangeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(StoneBowN.class)
			.t("name", "普通的绑石弓")
			.t("desc", "将化石绑在弓背上的木弓，可以发射箭矢。");
	}
 public StoneBowN() { super(2, Variant.NORMAL, SpecificPlaceHolderDict.SOMETHING_0); } }
