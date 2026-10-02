package pd.items.equipment.weapon.ranges;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;
public class AlloyBowN extends RangeWeapon {
	{
		image = EquipmentEquipWeaponBasicWeaponDict.CROSSBOW;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(AlloyBowN.class)
			.t("name", "普通的合金弩")
			.t("desc", "使用多种金属打造而成的弩，可以发射箭矢。");
	}


 public AlloyBowN() { super(4, Variant.NORMAL, SpecificPlaceHolderDict.SOMETHING_0); } }
