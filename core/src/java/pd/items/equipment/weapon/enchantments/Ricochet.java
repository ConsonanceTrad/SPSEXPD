/*
 * Special Surprise Pixel Dungeon, GPLv3 or later.
 */
package pd.items.equipment.weapon.enchantments;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.items.equipment.weapon.Weapon;
import pd.sprites.ItemSprite;
import render.utils.math.Random;

import pd.messages.InlineText;

/**
 * SPSEXPD: 枪械专属附魔「弹射」——命中时有几率把子弹弹向目标附近的另一名敌人，造成部分伤害。
 */
public class Ricochet extends Weapon.Enchantment {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Ricochet.class)
			.t("name", "弹射%s")
			.t("desc", "弹射武器在命中有几率把子弹弹向目标附近的其他敌人，造成部分伤害。")
			.t("elestrike_desc", "武器拥有弹射附魔时，元素打击会弹射到范围内除主要目标外的每个敌人。");
	}

	private static ItemSprite.Glowing ORANGE = new ItemSprite.Glowing( 0xFF8800 );

	@Override
	public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		int level = Math.max(0, weapon.buffedLvl());
		float procChance = (level + 1f) / (level + 5f) * procChanceMultiplier(attacker);
		if (Random.Float() < procChance && Dungeon.level != null) {
			Char target = null;
			for (Char c : Actor.chars()) {
				if (c != null && c != attacker && c != defender && c.isAlive()
						&& c.alignment != attacker.alignment
						&& Dungeon.level.distance(defender.pos, c.pos) <= 2) {
					target = c;
					break;
				}
			}
			if (target != null) {
				target.damage(Math.max(1, Math.round(damage * 0.5f)), this);
			}
		}
		return damage;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return ORANGE;
	}
}
