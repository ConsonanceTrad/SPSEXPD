package pd.items.equipment.weapon.ranges;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
public class MetalBowN extends RangeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(MetalBowN.class)
			.t("name", "普通的金属弓")
			.t("desc", "使用金属浇筑而成的弓，可以发射箭矢。");
	}
 public MetalBowN() { super(3, Variant.NORMAL, SpecificPlaceHolderDict.SOMETHING_0); } }
