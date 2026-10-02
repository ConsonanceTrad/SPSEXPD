package pd.items.equipment.weapon.guns;

import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;
import pd.messages.InlineText;


public class GunD extends GunWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(GunD.class)
			.t("name", "D型枪械")
			.t("desc", "出自皇家工坊的精准滑膛枪械，被投入矮人城区的战争环境进行测试。");
	}



	{ image = EquipmentEquipWeaponBasicWeaponDict.GUN_4; }
	public GunD() { super(4, 5); }
}
