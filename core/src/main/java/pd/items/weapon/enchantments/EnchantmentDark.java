/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.enchantments;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Terror;
import pd.effects.particles.ShadowParticle;
import pd.items.weapon.Weapon;
import pd.sprites.ItemSprite;
import com.watabou.utils.Random;

import static pd.actors.damagetype.DamageType.DARK_DAMAGE;

public class EnchantmentDark extends SpsEnchantment {
	private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0x000000);
	@Override public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		elementalDamage(weapon, attacker, defender, 0.75f, DARK_DAMAGE);
		if (Random.Int(4) == 1) {
			Buff.prolong(defender, Terror.class, 3f);
			if (defender.sprite != null) defender.sprite.emitter().burst(ShadowParticle.UP, 5);
		}
		return damage;
	}
	@Override public ItemSprite.Glowing glowing() { return BLACK; }
}
