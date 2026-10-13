package pd.items.equipment.weapon.guns;

import pd.atlas.items.EquipmentEquipWeaponGunDict;
import pd.messages.InlineText;

/** SPSEXPD: 手枪·闪电M——弹匣最多的速射手枪。 */
public class LightningM extends GunWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(LightningM.class)
			.t("name", "闪电M")
			.t("desc", "击锤经过轻量化改造的手枪，出弹快得只剩一道残影。");
	}

	{ image = EquipmentEquipWeaponGunDict.LIGHTNING_M; }
	public LightningM() {
		super(GunArchetype.PISTOL);
		fullCharge = 9;      //SPSEXPD: 个体特色——弹匣 9
		perfectSTR = 12;     //SPSEXPD: 个体特色——完美力量 12
	}
}
