package pd.items.equipment.weapon.ranges;

import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;
import pd.messages.InlineText;
public class WoodenBowN extends RangeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WoodenBowN.class)
			.t("name", "普通的木弓")
			.t("desc", "普通的木制弓，可以发射箭矢。");
	}
 public WoodenBowN() { super(1, Variant.NORMAL, EquipmentEquipWeaponBasicWeaponDict.WOODEN_BOW); } }
