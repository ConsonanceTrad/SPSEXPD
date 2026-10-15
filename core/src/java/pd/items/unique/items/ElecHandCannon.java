/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.atlas.items.EquipmentEquipWeaponGunDict;
import pd.items.equipment.weapon.guns.GunArchetype;
import pd.items.equipment.weapon.guns.GunWeapon;
import pd.items.unique.UniqueItem;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;

/**
 * SPSEXPD: 电磁手炮 —— 星兵一档（面具 B 路线）的武器。
 *
 * <p>沿用现有枪械体系：充能型（{@link GunArchetype#CHARGE}），用通用弹匣装填。</p>
 */
public class ElecHandCannon extends GunWeapon implements UniqueItem {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.SOLDIER_GUN_T1_2_CANNON;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(ElecHandCannon.class)
			.t("name", "电磁手炮")
			.t("desc", "一门把电磁线圈塞进短炮管的怪家伙。它会在你尚未开火时自行积蓄电荷，弹匣耗尽时用通用弹匣装填即可。");
	}

	{
		//SPSEXPD: 暂借用现成枪械图标
		image = EquipmentEquipWeaponGunDict.GUN_A;
	}

	public ElecHandCannon() {
		super(GunArchetype.CHARGE);
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
