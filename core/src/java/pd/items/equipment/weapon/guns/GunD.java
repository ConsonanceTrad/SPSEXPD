package pd.items.equipment.weapon.guns;

import pd.atlas.items.EquipmentEquipWeaponGunDict;
import pd.messages.InlineText;

/** SPSEXPD: 狙击枪·大主教（原 D 型枪械重铸）——自带弹道穿透、精准范围 12、装填 3 回合。 */
public class GunD extends GunWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(GunD.class)
			.t("name", "大主教")
			.t("desc", "出自皇家工坊的重型狙击枪，子弹穿透目标后仍会继续飞行，直到撞上墙壁。");
	}

	{ image = EquipmentEquipWeaponGunDict.GUN_D; }
	public GunD() { super(GunArchetype.SNIPER); }
}
