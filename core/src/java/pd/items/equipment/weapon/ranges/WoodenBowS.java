package pd.items.equipment.weapon.ranges;

import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;
import pd.messages.InlineText;
public class WoodenBowS extends RangeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WoodenBowS.class)
			.t("name", "轻巧的木弓")
			.t("desc", "普通的木制弓，但是比较轻巧。");
	}
 public WoodenBowS() { super(1, Variant.LIGHT, EquipmentEquipWeaponBasicWeaponDict.WOODEN_BOW); } }
