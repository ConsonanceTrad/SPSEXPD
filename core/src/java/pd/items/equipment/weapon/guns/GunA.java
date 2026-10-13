package pd.items.equipment.weapon.guns;

import pd.atlas.items.EquipmentEquipWeaponGunDict;
import pd.messages.InlineText;

/** SPSEXPD: 手枪·沉默之鹰（原 A 型枪械重铸）——单发点射、弹匣 7、装填 1 回合。 */
public class GunA extends GunWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(GunA.class)
			.t("name", "沉默之鹰")
			.t("desc", "枪管加装了消音结构的制式手枪，单发点射，枪声低沉而短促。");
	}

	{ image = EquipmentEquipWeaponGunDict.GUN_A; }
	public GunA() { super(GunArchetype.PISTOL); }
}
