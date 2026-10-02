package pd.items.equipment.weapon.ranges;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
public class AlloyBowS extends RangeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(AlloyBowS.class)
			.t("name", "轻巧的合金弩")
			.t("desc", "使用多种金属打造而成的弩，但是比较轻巧。");
	}


 public AlloyBowS() { super(4, Variant.LIGHT, SpecificPlaceHolderDict.SOMETHING_0); } }
