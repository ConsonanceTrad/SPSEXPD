package pd.items.equipment.weapon.ranges;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;
public class StoneBowR extends RangeWeapon {
	{
		image = EquipmentEquipWeaponBasicWeaponDict.WOODEN_BOW;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(StoneBowR.class)
			.t("name", "沉重的绑石弓")
			.t("desc", "将化石绑在弓背上的木弓，但是有些沉重。");
	}


 public StoneBowR() { super(2, Variant.HEAVY, SpecificPlaceHolderDict.SOMETHING_0); } }
