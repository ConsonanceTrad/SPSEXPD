package pd.items.equipment.weapon.guns;

import pd.atlas.items.EquipmentEquipWeaponGunDict;
import pd.messages.InlineText;

/** SPSEXPD: 冲锋枪·正午——弹匣较小的精准冲锋枪。 */
public class HighNoon extends GunWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(HighNoon.class)
			.t("name", "正午")
			.t("desc", "为烈日下的枪战而生的冲锋枪，弹匣不长，但抬枪即中。");
	}

	{ image = EquipmentEquipWeaponGunDict.HIGH_NOON; }
	public HighNoon() {
		super(GunArchetype.SMG);
		fullCharge = 15;     //SPSEXPD: 个体特色——弹匣 15
		perfectSTR = 17;     //SPSEXPD: 个体特色——完美力量 17
		effectiveRange = 5;  //SPSEXPD: 个体特色——精准范围 5
	}
}
