package pd.items.equipment.weapon.ranges;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;
public class MetalBowR extends RangeWeapon {
	{
		image = EquipmentEquipWeaponBasicWeaponDict.COMPOSITE_BOW;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(MetalBowR.class)
			.t("name", "沉重的金属弓")
			.t("desc", "使用金属浇筑而成的弓，但是有些沉重。");
	}


 public MetalBowR() { super(3, Variant.HEAVY, SpecificPlaceHolderDict.SOMETHING_0); } }
