/*
 * Special Surprise Pixel Dungeon, GPLv3 or later.
 */
package pd.items.equipment.weapon.enchantments;

import pd.actors.Char;
import pd.actors.buffs.ArmorBreak;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Slow;
import pd.actors.damagetype.DamageType;
import pd.items.equipment.weapon.Weapon;
import pd.sprites.ItemSprite;
import render.utils.math.Random;

import pd.messages.InlineText;

/**
 * SPSEXPD: 枪械专属附魔「催眠」——由原特殊弹药「催眠弹」转化而来。
 * 命中时击碎目标护甲并使其减速。
 */
public class Hypnotic extends Weapon.Enchantment {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Hypnotic.class)
			.t("name", "催眠%s")
			.t("desc", "催眠武器在命中有几率击碎敌人的护甲，并使其陷入迟缓。")
			.t("elestrike_desc", "武器拥有催眠附魔时，元素打击会击碎并迟缓范围内所有敌人。");
	}

	private static ItemSprite.Glowing TEAL = new ItemSprite.Glowing( 0x33CCCC );

	@Override
	public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		if (!defender.isAlive()) return damage;
		int level = Math.max(0, weapon.buffedLvl());
		float procChance = (level + 1f) / (level + 4f) * procChanceMultiplier(attacker);
		if (Random.Float() < procChance) {
			defender.damage(Math.max(1, Math.round(damage * 0.2f)), DamageType.DARK_DAMAGE);
			Buff.prolong(defender, ArmorBreak.class, 6f).level(25);
			Buff.prolong(defender, Slow.class, 3f);
		}
		return damage;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return TEAL;
	}
}
