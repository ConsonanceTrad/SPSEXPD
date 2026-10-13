/*
 * Special Surprise Pixel Dungeon, GPLv3 or later.
 */
package pd.items.equipment.weapon.enchantments;

import pd.actors.Char;
import pd.actors.buffs.ArmorBreak;
import pd.actors.buffs.Buff;
import pd.items.equipment.weapon.Weapon;
import pd.sprites.ItemSprite;
import render.utils.math.Random;

import pd.messages.InlineText;

/**
 * SPSEXPD: 枪械专属附魔「穿甲」——命中时击碎目标护甲并造成一段无视部分防护的追加伤害。
 * 与弹道穿透（pierce）无关：这里指的是打穿护甲。
 */
public class Puncturing extends Weapon.Enchantment {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Puncturing.class)
			.t("name", "穿甲%s")
			.t("desc", "穿甲武器在命中有几率击碎敌人的护甲，并造成一段额外的伤害。")
			.t("elestrike_desc", "武器拥有穿甲附魔时，元素打击会额外击碎范围内所有敌人的护甲。");
	}

	private static ItemSprite.Glowing STEEL = new ItemSprite.Glowing( 0x99AABB );

	@Override
	public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		int level = Math.max(0, weapon.buffedLvl());
		float procChance = (level + 1f) / (level + 4f) * procChanceMultiplier(attacker);
		if (defender.isAlive() && Random.Float() < procChance) {
			Buff.prolong(defender, ArmorBreak.class, 5f).level(20 + 5 * level);
			defender.damage(Math.max(1, Math.round(damage * (0.15f + 0.05f * level))), this);
		}
		return damage;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return STEEL;
	}
}
