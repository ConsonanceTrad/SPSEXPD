package pd.items.equipment.weapon.guns;

import pd.atlas.items.EquipmentEquipWeaponGunDict;
import pd.messages.InlineText;

/** SPSEXPD: 手枪·加兰德——弹匣最浅、但精准范围最远的手枪。 */
public class Garand extends GunWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Garand.class)
			.t("name", "加兰德")
			.t("desc", "老兵惯用的制式手枪，装弹不多，却在远距离依旧稳当。");
	}

	{ image = EquipmentEquipWeaponGunDict.GARAND; }
	public Garand() {
		super(GunArchetype.PISTOL);
		fullCharge = 5;      //SPSEXPD: 个体特色——弹匣 5
		perfectSTR = 14;     //SPSEXPD: 个体特色——完美力量 14
		effectiveRange = 6;  //SPSEXPD: 个体特色——精准范围 6
	}
}
