/*
 * Special Surprise Pixel Dungeon, GPLv3 or later.
 */
package pd.items.equipment.weapon.enchantments;

import pd.actors.Char;
import pd.actors.damagetype.DamageType;
import pd.items.equipment.weapon.Weapon;
import pd.sprites.ItemSprite;
import render.utils.math.Random;

import pd.messages.InlineText;

/**
 * SPSEXPD: 枪械专属附魔「陨星」——由原特殊弹药「陨星弹」转化而来。
 * 命中有几率造成一段巨额暗影伤害（视目标生命上限而定）。
 */
public class Starfall extends Weapon.Enchantment {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Starfall.class)
			.t("name", "陨星%s")
			.t("desc", "陨星武器在命中有较小几率降下陨星，根据目标的生命上限造成一段巨额暗影伤害。")
			.t("elestrike_desc", "武器拥有陨星附魔时，元素打击有几率对范围内每个敌人降下陨星。");
	}

	private static ItemSprite.Glowing VIOLET = new ItemSprite.Glowing( 0xAA44FF );

	@Override
	public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		if (!defender.isAlive()) return damage;
		int level = Math.max(0, weapon.buffedLvl());
		float procChance = 0.05f + 0.02f * level;
		if (Random.Float() < procChance) {
			boolean boss = Char.hasProp(defender, Char.Property.BOSS) || Char.hasProp(defender, Char.Property.MINIBOSS);
			int extra = boss
					? Random.IntRange(defender.HT / 8, defender.HT / 4 + 1)
					: Random.IntRange(defender.HT / 2, defender.HT + 1);
			defender.damage(Math.max(1, extra), DamageType.DARK_DAMAGE);
		} else {
			defender.damage(Math.max(1, Math.round(damage * 0.4f)), DamageType.DARK_DAMAGE);
		}
		return damage;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return VIOLET;
	}
}
