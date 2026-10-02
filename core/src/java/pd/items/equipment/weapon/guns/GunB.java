package pd.items.equipment.weapon.guns;

import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;
import pd.messages.InlineText;


public class GunB extends GunWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(GunB.class)
			.t("name", "B型枪械")
			.t("desc", "高塔基于下水道试验结果制作的可靠双管手枪，被投入监狱环境进行测试。");
	}

	{ image = EquipmentEquipWeaponBasicWeaponDict.GUN_4; }
	public GunB() { super(2, 4); }
}
