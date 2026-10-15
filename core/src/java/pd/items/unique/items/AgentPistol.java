/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.atlas.items.EquipmentEquipWeaponGunDict;
import pd.items.equipment.weapon.guns.GunArchetype;
import pd.items.equipment.weapon.guns.GunWeapon;
import pd.items.unique.UniqueItem;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;

/**
 * SPSEXPD: 特工手枪 —— 盗贼一档（面具 B 路线）的枪械。
 *
 * <p>一种_射程更远、但伤害更低_的手枪。</p>
 */
public class AgentPistol extends GunWeapon implements UniqueItem {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.ROGUE_CLOAK_T1_2_AGENT;

	public static final int RANGE = 6;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(AgentPistol.class)
			.t("name", "特工手枪")
			.t("desc", "一把装了加长枪管的制式手枪，打得比同类都远——代价是每一发的威力都轻飘飘的。");
	}

	{
		//SPSEXPD: 暂借用现成枪械图标
		image = EquipmentEquipWeaponGunDict.GUN_A;
	}

	public AgentPistol() {
		super(GunArchetype.PISTOL);
		//SPSEXPD: 射程更远
		effectiveRange = RANGE;
	}

	//SPSEXPD: 伤害更低
	@Override public int min(int lvl) { return 2 + lvl; }
	@Override public int max(int lvl) { return 5 + 2 * lvl; }

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
