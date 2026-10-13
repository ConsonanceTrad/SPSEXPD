package pd.items.equipment.weapon.guns;

import pd.atlas.items.EquipmentEquipWeaponGunDict;
import pd.messages.InlineText;

/** SPSEXPD: 手枪·解放者——弹匣更大、完美力量更低的轻便手枪。 */
public class Liberator extends GunWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Liberator.class)
			.t("name", "解放者")
			.t("desc", "铸造工会为起义者赶制的手枪，弹匣意外地充裕，握起来却轻若无物。");
	}

	{ image = EquipmentEquipWeaponGunDict.LIBERATOR; }
	public Liberator() {
		super(GunArchetype.PISTOL);
		fullCharge = 8;      //SPSEXPD: 个体特色——弹匣 8
		perfectSTR = 11;     //SPSEXPD: 个体特色——完美力量 11
	}
}
