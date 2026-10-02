package pd.items.equipment.weapon.ranges;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;
public class PVCBowR extends RangeWeapon {
	{
		image = EquipmentEquipWeaponBasicWeaponDict.CROSSBOW;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PVCBowR.class)
			.t("name", "沉重的复合纤维弩")
			.t("desc", "经过复杂工序加工而成的弩，但是有些沉重。");
	}


 public PVCBowR() { super(5, Variant.HEAVY, SpecificPlaceHolderDict.SOMETHING_0); } }
