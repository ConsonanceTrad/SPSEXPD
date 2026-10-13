package pd.items.equipment.weapon.guns;

import pd.atlas.items.EquipmentEquipWeaponGunDict;
import pd.messages.InlineText;

/** SPSEXPD: 手枪·H.O.W.L——枪声传播最远的制式手枪。 */
public class Howl extends GunWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Howl.class)
			.t("name", "H.O.W.L")
			.t("desc", "枪口制退器被整体拆除的制式手枪，每一发都在地牢里嘶吼。");
	}

	{ image = EquipmentEquipWeaponGunDict.HOWL; }
	public Howl() {
		super(GunArchetype.PISTOL);
		fullCharge = 7;      //SPSEXPD: 个体特色——弹匣 7
		perfectSTR = 13;     //SPSEXPD: 个体特色——完美力量 13
		noiseRadius = 6;     //SPSEXPD: 个体特色——枪声传播更远
	}
}
