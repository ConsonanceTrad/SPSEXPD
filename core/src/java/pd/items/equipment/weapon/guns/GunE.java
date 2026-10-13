package pd.items.equipment.weapon.guns;

import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;
import pd.messages.InlineText;

/** SPSEXPD: 狙击枪·至高无上（原 E 型枪械重铸）——弹匣更大、单发伤害更高的穿透狙击枪。 */
public class GunE extends GunWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(GunE.class)
			.t("name", "至高无上")
			.t("desc", "恶魔大厅中缴获的重型狙击枪，弹匣更为充裕，一发足以贯穿整列敌人。");
	}

	{ image = EquipmentEquipWeaponBasicWeaponDict.GUN_4; }
	public GunE() {
		super(GunArchetype.SNIPER);
		fullCharge = 6; //SPSEXPD: 个体特色——弹匣比基准多一发
	}
}
