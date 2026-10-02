/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.enchantments;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.effects.particles.FlameParticle;
import pd.items.equipment.weapon.Weapon;
import pd.sprites.ItemSprite;
import render.utils.math.Random;

import static pd.actors.damagetype.DamageType.FIRE_DAMAGE;

public class EnchantmentFire extends SpsEnchantment {
	private static final ItemSprite.Glowing RED = new ItemSprite.Glowing(0xCC0000);
	@Override public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		elementalDamage(weapon, attacker, defender, 0.25f, FIRE_DAMAGE);
		if (Random.Int(4) >= 1) {
			Buff.affect(defender, Burning.class).reignite(defender, 3f);
			if (defender.sprite != null) defender.sprite.emitter().burst(FlameParticle.FACTORY, 5);
		}
		return damage;
	}
	@Override public ItemSprite.Glowing glowing() { return RED; }
}
