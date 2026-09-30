/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.enchantments;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.FrostIce;
import pd.effects.particles.SnowParticle;
import pd.items.weapon.Weapon;
import pd.sprites.ItemSprite;
import watabou.utils.Random;

import static pd.actors.damagetype.DamageType.ICE_DAMAGE;

public class EnchantmentIce2 extends SpsEnchantment {
	private static final ItemSprite.Glowing BLUE = new ItemSprite.Glowing(0x0044FF);
	@Override public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		elementalDamage(weapon, attacker, defender, 0.25f, ICE_DAMAGE);
		if (Random.Int(3) >= 1) Buff.affect(defender, FrostIce.class).level(legacyRoll(weapon, attacker));
		if (defender.sprite != null) defender.sprite.emitter().burst(SnowParticle.FACTORY, 5);
		return damage;
	}
	@Override public ItemSprite.Glowing glowing() { return BLUE; }
}
