package pd.items.equipment.weapon.ranges;

import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;
import pd.messages.InlineText;
public class WoodenBowR extends RangeWeapon {
	{
		image = EquipmentEquipWeaponBasicWeaponDict.WOODEN_BOW;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WoodenBowR.class)
			.t("name", "沉重的木弓")
			.t("desc", "普通的木制弓，但是有些沉重。");
	}


 public WoodenBowR() { super(1, Variant.HEAVY, EquipmentEquipWeaponBasicWeaponDict.WOODEN_BOW); } }
