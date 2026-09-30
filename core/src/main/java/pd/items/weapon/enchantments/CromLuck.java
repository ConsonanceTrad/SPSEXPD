/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.enchantments;

import pd.actors.Char;
import pd.items.weapon.Weapon;
import pd.sprites.ItemSprite;

/** Re-rolls the complete attack damage and applies the best excess roll. */
public class CromLuck extends Weapon.Enchantment {
	@Override public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		int best = damage;
		for (int i = 0; i <= Math.max(0, weapon.level()); i++) best = Math.max(best, attacker.damageRoll());
		if (best > damage) defender.damage(best - damage, weapon);
		return damage;
	}
	@Override public ItemSprite.Glowing glowing() { return new ItemSprite.Glowing(0x800000); }
}
