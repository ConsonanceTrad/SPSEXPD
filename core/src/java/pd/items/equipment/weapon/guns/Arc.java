package pd.items.equipment.weapon.guns;

import pd.atlas.items.EquipmentEquipWeaponGunDict;
import pd.messages.InlineText;

/** SPSEXPD: 冲锋枪·A.R.C——单次击发 5 发的连射冲锋枪。 */
public class Arc extends GunWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Arc.class)
			.t("name", "A.R.C")
			.t("desc", "电弧供弹的冲锋枪，一次扣动扳机便连泼五发，弹道像一道蓝色弧线。");
	}

	{ image = EquipmentEquipWeaponGunDict.ARC; }
	public Arc() {
		super(GunArchetype.SMG);
		fullCharge = 20;     //SPSEXPD: 个体特色——弹匣 20
		shotsPerFire = 5;    //SPSEXPD: 个体特色——单次击发 5 发
		perfectSTR = 16;     //SPSEXPD: 个体特色——完美力量 16
	}
}
