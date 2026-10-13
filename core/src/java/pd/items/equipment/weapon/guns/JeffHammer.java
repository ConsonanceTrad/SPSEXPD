package pd.items.equipment.weapon.guns;

import pd.atlas.items.EquipmentEquipWeaponGunDict;
import pd.messages.InlineText;

/** SPSEXPD: 霰弹枪·杰夫哈默——枪声传播最远的霰弹枪。 */
public class JeffHammer extends GunWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(JeffHammer.class)
			.t("name", "杰夫哈默")
			.t("desc", "炮管粗短的民间改装霰弹枪，每开一枪整层地牢都听得见。");
	}

	{ image = EquipmentEquipWeaponGunDict.JEFF_HAMMER; }
	public JeffHammer() {
		super(GunArchetype.SHOTGUN);
		fullCharge = 4;      //SPSEXPD: 个体特色——弹匣 4
		perfectSTR = 19;     //SPSEXPD: 个体特色——完美力量 19
		noiseRadius = 8;     //SPSEXPD: 个体特色——枪声传播更远
	}
}
