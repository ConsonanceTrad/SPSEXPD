/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.enchantments;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Shocked;
import pd.items.equipment.weapon.Weapon;
import pd.sprites.ItemSprite;
import render.utils.math.Random;

import static pd.actors.damagetype.DamageType.SHOCK_DAMAGE;
import pd.messages.InlineText;

public class EnchantmentShock2 extends SpsEnchantment {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(EnchantmentShock2.class)
			.t("name", "电震%s")
			.t("desc", "电震附魔将造成少量的雷属性伤害，并有几率给目标上静电效果。");
	}



	private static final ItemSprite.Glowing GREEN = new ItemSprite.Glowing(0x00FF00);
	@Override public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		elementalDamage(weapon, attacker, defender, 0.25f, SHOCK_DAMAGE);
		if (Random.Int(4) == 1) Buff.affect(defender, Shocked.class).level(3);
		return damage;
	}
	@Override public ItemSprite.Glowing glowing() { return GREEN; }
}
