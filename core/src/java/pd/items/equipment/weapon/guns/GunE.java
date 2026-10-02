package pd.items.equipment.weapon.guns;

import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;
import pd.messages.InlineText;


public class GunE extends GunWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(GunE.class)
			.t("name", "E型枪械")
			.t("desc", "伤害巨大且能击退敌人的重型迫击炮，被投入恶魔大厅进行测试。");
	}

	{ image = EquipmentEquipWeaponBasicWeaponDict.GUN_4; }
	public GunE() { super(5, 6); }
}
