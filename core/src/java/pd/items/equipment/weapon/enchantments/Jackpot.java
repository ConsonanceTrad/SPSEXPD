/*
 * Special Surprise Pixel Dungeon, GPLv3 or later.
 */
package pd.items.equipment.weapon.enchantments;

import pd.Dungeon;
import pd.actors.Char;
import pd.items.equipment.weapon.Weapon;
import pd.sprites.ItemSprite;
import render.utils.math.Random;

import pd.messages.InlineText;

/**
 * SPSEXPD: 枪械专属附魔「彩票」——由原特殊弹药「彩票弹」转化而来。
 * 命中时消耗少量金币，换取一段随机的额外伤害。
 */
public class Jackpot extends Weapon.Enchantment {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Jackpot.class)
			.t("name", "彩票%s")
			.t("desc", "彩票武器在命中有几率消耗少量金币，换取一段随机的额外伤害。")
			.t("elestrike_desc", "武器拥有彩票附魔时，元素打击会为范围内每个敌人抽取一张彩票。");
	}

	private static ItemSprite.Glowing GOLD = new ItemSprite.Glowing( 0xFFD700 );

	@Override
	public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		if (!defender.isAlive() || Dungeon.gold <= 0) return damage;
		int level = Math.max(0, weapon.buffedLvl());
		float procChance = (level + 1f) / (level + 3f) * procChanceMultiplier(attacker);
		if (Random.Float() < procChance) {
			int cost = Math.max(1, Dungeon.gold / 100);
			Dungeon.gold -= cost;
			int extra = Math.max(1, Math.round(cost * Random.Float(0.25f, 2f)));
			defender.damage(extra, this);
		}
		return damage;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return GOLD;
	}
}
