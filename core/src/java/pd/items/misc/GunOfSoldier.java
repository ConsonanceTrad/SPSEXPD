/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.actors.Char;
import pd.atlas.items.EquipmentNonEquipDict;
import pd.items.equipment.weapon.guns.GunArchetype;
import pd.items.equipment.weapon.guns.GunWeapon;
import pd.messages.InlineText;

/**
 * SPSEXPD: 星兵开局电磁手枪——「充能型枪械」（GunArchetype.CHARGE）：
 * 弹匣 3 发、可用通用弹匣装填，且在「本回合未开火」时每回合自然回充 0.5 发（显示取整）。
 * 命中时按目标「已损失生命比例」提高伤害（乘区：× (1 + 损失比例)）。unique、不可升级、需要装备后使用。
 */
public class GunOfSoldier extends GunWeapon implements pd.items.unique.UniqueItem {

	/** SPSEXPD: 特殊物品编号（见 UniqueItemId）。 */
	public static final pd.items.unique.UniqueItemId UNIQUE_ID = pd.items.unique.UniqueItemId.SOLDIER_GUN_BASE_A;

	@Override public pd.items.unique.UniqueItemId uniqueId() { return UNIQUE_ID; }

	//SPSEXPD: 特殊枪械——不使用通用弹道贴图，保持原有贴图
	@Override protected boolean usesDefaultBallistic() { return false; }
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(GunOfSoldier.class)
			.t("name", "脉冲手枪")
			.t("desc", "来自未来的先进脉冲手枪，搭载了虚空充能与扫描模块。\n\n可以随时间充能，且对伤势越重的敌人伤害越高。");
	}

	/** SPSEXPD: 弹匣容量（发）——与 GunArchetype.CHARGE 的基准一致。 */
	public static final int MAGAZINE = 3;

	{
		image = EquipmentNonEquipDict.ELECTROMAGNETIC_PISTOL;
		unique = true;
		bones = false;
	}

	public GunOfSoldier() {
		super(GunArchetype.CHARGE);
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 30 * quantity; }

	/**
	 * SPSEXPD: 命中时按目标「已损失生命比例」提高伤害——走**乘区**（damage × (1 + 损失比例)，比例∈[0,1]），
	 * 而不是额外叠加一段固定数值（加算会数值膨胀）。并进主伤害一起结算，只飘一个数字。
	 */
	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (defender != null && defender.isAlive() && defender.HT > 0) {
			float missing = (defender.HT - defender.HP) / (float) defender.HT;
			missing = Math.max(0f, Math.min(1f, missing));
			damage = Math.round(damage * (1f + missing));
		}
		return super.proc(attacker, defender, damage);
	}
}
