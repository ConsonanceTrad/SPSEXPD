package pd.items.equipment.weapon.guns;

import pd.atlas.items.EquipmentEquipWeaponGunDict;
import pd.messages.InlineText;

/** SPSEXPD: 冲锋枪·寻雷（原 B 型枪械重铸）——单次击发 4 发、弹匣 20、装填 2 回合。 */
public class GunB extends GunWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(GunB.class)
			.t("name", "寻雷")
			.t("desc", "射速极快的冲锋枪，一次扣动扳机便泼出四发子弹，弹雨如追雷而去。");
	}

	{ image = EquipmentEquipWeaponGunDict.GUN_B; }
	public GunB() { super(GunArchetype.SMG); }
}
