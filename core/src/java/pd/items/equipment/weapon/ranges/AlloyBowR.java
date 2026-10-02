package pd.items.equipment.weapon.ranges;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
public class AlloyBowR extends RangeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(AlloyBowR.class)
			.t("name", "沉重的合金弩")
			.t("desc", "使用多种金属打造而成的弩，但是有些沉重。");
	}
 public AlloyBowR() { super(4, Variant.HEAVY, SpecificPlaceHolderDict.SOMETHING_0); } }
