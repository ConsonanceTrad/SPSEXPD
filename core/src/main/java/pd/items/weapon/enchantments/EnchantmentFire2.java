/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.enchantments;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Hot;
import pd.actors.buffs.Tar;
import pd.effects.particles.FlameParticle;
import pd.items.weapon.Weapon;
import pd.sprites.ItemSprite;
import watabou.utils.Random;

import static pd.actors.damagetype.DamageType.FIRE_DAMAGE;

public class EnchantmentFire2 extends SpsEnchantment {
	private static final ItemSprite.Glowing RED = new ItemSprite.Glowing(0xCC0000);
	@Override public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		elementalDamage(weapon, attacker, defender, 0.75f, FIRE_DAMAGE);
		if (defender.isAlive()) {
			Buff.prolong(defender, Hot.class, 3f);
			if (Random.Int(3) == 1) Buff.affect(defender, Tar.class);
			if (defender.sprite != null) defender.sprite.emitter().burst(FlameParticle.FACTORY, 5);
		}
		return damage;
	}
	@Override public ItemSprite.Glowing glowing() { return RED; }
}
