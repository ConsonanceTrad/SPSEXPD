package pd.items.equipment.weapon.guns;

import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;
import pd.messages.InlineText;


public class GunC extends GunWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(GunC.class)
			.t("name", "C型枪械")
			.t("desc", "适合狭窄空间的霰弹枪械，被投入洞穴环境进行压力测试。");
	}

	{ image = EquipmentEquipWeaponBasicWeaponDict.GUN_4; }
	public GunC() { super(3, 5); }
}
