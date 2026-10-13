package pd.items.equipment.weapon.guns;

import pd.atlas.items.EquipmentEquipWeaponGunDict;
import pd.messages.InlineText;

/** SPSEXPD: 霰弹枪·电磁霰弹枪（原 C 型枪械重铸）——弹匣 4、逐颗装填、精准范围极短。 */
public class GunC extends GunWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(GunC.class)
			.t("name", "电磁霰弹枪")
			.t("desc", "线圈加速的霰弹枪，弹丸在近距离拥有恐怖的穿透力，但射程极短。");
	}

	{ image = EquipmentEquipWeaponGunDict.GUN_C; }
	public GunC() { super(GunArchetype.SHOTGUN); }
}
