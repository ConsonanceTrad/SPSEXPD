/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.enchantments;

import pd.actors.Char;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Buff;
import pd.items.weapon.Weapon;
import pd.sprites.ItemSprite;
import render.utils.math.Random;

import static pd.actors.damagetype.DamageType.LIGHT_DAMAGE;

public class EnchantmentLight extends SpsEnchantment {
	private static final ItemSprite.Glowing YELLOW = new ItemSprite.Glowing(0xFFFF44);
	@Override public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		elementalDamage(weapon, attacker, defender, 0.75f, LIGHT_DAMAGE);
		if (Random.Int(3) >= 1) Buff.prolong(defender, Blindness.class, 4f);
		return damage;
	}
	@Override public ItemSprite.Glowing glowing() { return YELLOW; }
}
