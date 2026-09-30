/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.enchantments;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.LightShootAttack;
import pd.effects.Speck;
import pd.items.weapon.Weapon;
import pd.sprites.ItemSprite;
import render.utils.math.Random;

import static pd.actors.damagetype.DamageType.LIGHT_DAMAGE;

public class EnchantmentLight2 extends SpsEnchantment {
	private static final ItemSprite.Glowing YELLOW = new ItemSprite.Glowing(0xFFFF44);
	@Override public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		elementalDamage(weapon, attacker, defender, 0.25f, LIGHT_DAMAGE);
		if (Random.Int(3) == 1) Buff.affect(defender, LightShootAttack.class).level(5);
		if (defender.sprite != null) defender.sprite.emitter().burst(Speck.factory(Speck.LIGHT), 6);
		return damage;
	}
	@Override public ItemSprite.Glowing glowing() { return YELLOW; }
}
