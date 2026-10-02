/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.enchantments;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.ShadowCurse;
import pd.effects.particles.ShadowParticle;
import pd.items.equipment.weapon.Weapon;
import pd.sprites.ItemSprite;

import static pd.actors.damagetype.DamageType.DARK_DAMAGE;

public class EnchantmentDark2 extends SpsEnchantment {
	private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0x000000);
	@Override public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		Buff.affect(defender, ShadowCurse.class);
		elementalDamage(weapon, attacker, defender, 0.25f, DARK_DAMAGE);
		if (defender.sprite != null) defender.sprite.emitter().burst(ShadowParticle.UP, 5);
		return damage;
	}
	@Override public ItemSprite.Glowing glowing() { return BLACK; }
}
