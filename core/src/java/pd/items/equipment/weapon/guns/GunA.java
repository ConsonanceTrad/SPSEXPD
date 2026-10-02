package pd.items.equipment.weapon.guns;

import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;
import pd.messages.InlineText;


public class GunA extends GunWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(GunA.class)
			.t("name", "A型枪械")
			.t("desc", "高塔利用枪械零件制作的轻型实验手枪，被投入下水道环境进行测试。");
	}



	{ image = EquipmentEquipWeaponBasicWeaponDict.GUN_4; }
	public GunA() { super(1, 4); }
}
