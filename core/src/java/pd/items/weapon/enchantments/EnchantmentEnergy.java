/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.enchantments;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import pd.actors.buffs.DamageUp;
import pd.effects.Speck;
import pd.items.weapon.Weapon;
import pd.sprites.ItemSprite;

import static pd.actors.damagetype.DamageType.ENERGY_DAMAGE;

public class EnchantmentEnergy extends SpsEnchantment {
	private static final ItemSprite.Glowing GRAY = new ItemSprite.Glowing(0x888888);
	@Override public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		elementalDamage(weapon, attacker, defender, 0.25f, ENERGY_DAMAGE);
		if (attacker.buff(DamageUp.class) == null) {
			Buff.affect(attacker, DamageUp.class).level(legacyRoll(weapon, attacker));
		} else {
			Buff.affect(defender, Cripple.class, 3f);
		}
		if (attacker.sprite != null) attacker.sprite.emitter().start(Speck.factory(Speck.UP), 0.2f, 3);
		return damage;
	}
	@Override public ItemSprite.Glowing glowing() { return GRAY; }
}
