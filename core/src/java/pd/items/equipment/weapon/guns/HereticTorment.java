package pd.items.equipment.weapon.guns;

import pd.atlas.items.EquipmentEquipWeaponGunDict;
import pd.messages.InlineText;

/** SPSEXPD: 霰弹枪·异端酷刑——弹匣更深、完美力量更低的霰弹枪。 */
public class HereticTorment extends GunWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(HereticTorment.class)
			.t("name", "异端酷刑")
			.t("desc", "审判庭的处刑武器，装弹比同类多一发，专为近距离宣判。");
	}

	{ image = EquipmentEquipWeaponGunDict.HERETIC_TORMENT; }
	public HereticTorment() {
		super(GunArchetype.SHOTGUN);
		fullCharge = 5;      //SPSEXPD: 个体特色——弹匣 5
		perfectSTR = 17;     //SPSEXPD: 个体特色——完美力量 17
	}
}
