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
 * 命中时按目标已损失的生命造成额外伤害。unique、不可升级、需要装备后使用。
 */
public class GunOfSoldier extends GunWeapon {
	//SPSEXPD: 特殊枪械——不使用通用弹道贴图，保持原有贴图
	@Override protected boolean usesDefaultBallistic() { return false; }
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(GunOfSoldier.class)
			.t("name", "脉冲手枪")
			.t("desc", "来自未来的先进脉冲手枪，会根据目标已损失的生命造成额外伤害。\n\n弹匣会缓慢自然回充，也可以手动装填。\n\n需要装备后才能射击。");
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

	/** SPSEXPD: 按目标已损失的生命造成额外伤害。 */
	@Override
	protected void onBulletHit(Char attacker, Char defender, int damage) {
		int cap = Char.hasProp(defender, Char.Property.BOSS) || Char.hasProp(defender, Char.Property.MINIBOSS)
				? defender.HT / 6 : defender.HT / 3;
		defender.damage(Math.min(Math.max(0, defender.HT - defender.HP), cap), this);
	}
}
