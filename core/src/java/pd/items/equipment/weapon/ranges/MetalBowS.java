package pd.items.equipment.weapon.ranges;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
public class MetalBowS extends RangeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(MetalBowS.class)
			.t("name", "轻巧的金属弓")
			.t("desc", "使用金属浇筑而成的弓，但是比较轻巧。");
	}
 public MetalBowS() { super(3, Variant.LIGHT, SpecificPlaceHolderDict.SOMETHING_0); } }
