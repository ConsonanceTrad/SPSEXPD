/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.atlas.items.EquipmentEquipWeaponGunDict;
import pd.items.equipment.weapon.guns.GunArchetype;
import pd.items.equipment.weapon.guns.GunWeapon;
import pd.items.unique.UniqueItem;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;

/**
 * SPSEXPD: 无声手枪 —— 盗贼二档（王冠 B 路线）的枪械。
 *
 * <p>射程较远、伤害与普通手枪相当；_佩戴它会大幅降低枪声惊动的范围_。</p>
 */
public class SilentPistol extends GunWeapon implements UniqueItem {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.ROGUE_CLOAK_T2_2_SILENT;

	public static final int RANGE = 5;
	/** 枪声惊动半径（普通手枪为 4）。 */
	public static final int NOISE = 1;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(SilentPistol.class)
			.t("name", "无声手枪")
			.t("desc", "一把裹了消音套筒的手枪，开枪时只有闷闷的一声。\n\n它射程比普通手枪远，伤害与普通手枪相当，但_佩戴它会大幅降低枪声惊动的范围_（惊醒半径 %1$d 格）。");
	}

	{
		//SPSEXPD: 暂借用现成枪械图标
		image = EquipmentEquipWeaponGunDict.GUN_A;
	}

	public SilentPistol() {
		super(GunArchetype.PISTOL);
		//SPSEXPD: 射程更远 + 枪声极低
		effectiveRange = RANGE;
		noiseRadius = NOISE;
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
