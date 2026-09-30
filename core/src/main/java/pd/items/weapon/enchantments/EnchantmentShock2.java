/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.enchantments;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Shocked;
import pd.items.weapon.Weapon;
import pd.sprites.ItemSprite;
import com.watabou.utils.Random;

import static pd.actors.damagetype.DamageType.SHOCK_DAMAGE;

public class EnchantmentShock2 extends SpsEnchantment {
	private static final ItemSprite.Glowing GREEN = new ItemSprite.Glowing(0x00FF00);
	@Override public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		elementalDamage(weapon, attacker, defender, 0.25f, SHOCK_DAMAGE);
		if (Random.Int(4) == 1) Buff.affect(defender, Shocked.class).level(3);
		return damage;
	}
	@Override public ItemSprite.Glowing glowing() { return GREEN; }
}
