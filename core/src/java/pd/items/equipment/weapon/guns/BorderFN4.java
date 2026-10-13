package pd.items.equipment.weapon.guns;

import pd.atlas.items.EquipmentEquipWeaponGunDict;
import pd.messages.InlineText;

/** SPSEXPD: 霰弹枪·边界 FN-4——精准范围略远的霰弹枪。 */
public class BorderFN4 extends GunWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(BorderFN4.class)
			.t("name", "边界 FN-4")
			.t("desc", "边境哨所配发的制式霰弹枪，弹匣更深，散布也收得更紧一些。");
	}

	{ image = EquipmentEquipWeaponGunDict.BORDER_FN4; }
	public BorderFN4() {
		super(GunArchetype.SHOTGUN);
		fullCharge = 6;      //SPSEXPD: 个体特色——弹匣 6
		perfectSTR = 18;     //SPSEXPD: 个体特色——完美力量 18
		effectiveRange = 3;  //SPSEXPD: 个体特色——精准范围 3
	}
}
