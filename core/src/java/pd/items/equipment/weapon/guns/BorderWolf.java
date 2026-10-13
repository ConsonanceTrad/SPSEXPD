package pd.items.equipment.weapon.guns;

import pd.atlas.items.EquipmentEquipWeaponGunDict;
import pd.messages.InlineText;

/** SPSEXPD: 狙击枪·边境之狼——弹匣更小、精准范围更远的穿透狙击枪。 */
public class BorderWolf extends GunWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(BorderWolf.class)
			.t("name", "边境之狼")
			.t("desc", "边境猎手传下来的狙击枪，弹匣只够装填几发，却能在最远的距离上穿透整列敌人。");
	}

	{ image = EquipmentEquipWeaponGunDict.BORDER_WOLF; }
	public BorderWolf() {
		super(GunArchetype.SNIPER);
		fullCharge = 4;      //SPSEXPD: 个体特色——弹匣 4
		perfectSTR = 21;     //SPSEXPD: 个体特色——完美力量 21
		effectiveRange = 14; //SPSEXPD: 个体特色——精准范围 14
	}
}
