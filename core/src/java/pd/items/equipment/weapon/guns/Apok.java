package pd.items.equipment.weapon.guns;

import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;
import pd.messages.InlineText;

/** SPSEXPD: 霰弹枪·A.P.O.K——弹匣最浅、完美力量最高的重型霰弹枪。 */
public class Apok extends GunWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Apok.class)
			.t("name", "A.P.O.K")
			.t("desc", "据称是某场灾难后的最后一件样品，弹匣浅得可怜，后坐力却足以让人踉跄。");
	}

	{ image = EquipmentEquipWeaponBasicWeaponDict.SHOTGUN; } //SPSEXPD: 占位图标，待美术替换
	public Apok() {
		super(GunArchetype.SHOTGUN);
		fullCharge = 3;      //SPSEXPD: 个体特色——弹匣 3
		perfectSTR = 20;     //SPSEXPD: 个体特色——完美力量 20
	}
}
