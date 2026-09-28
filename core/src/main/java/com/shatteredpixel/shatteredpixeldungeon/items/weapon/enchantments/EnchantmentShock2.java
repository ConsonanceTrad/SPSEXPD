/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Shocked;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

import static com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType.SHOCK_DAMAGE;

public class EnchantmentShock2 extends SpsEnchantment {
	private static final ItemSprite.Glowing GREEN = new ItemSprite.Glowing(0x00FF00);
	@Override public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		elementalDamage(weapon, attacker, defender, 0.25f, SHOCK_DAMAGE);
		if (Random.Int(4) == 1) Buff.affect(defender, Shocked.class).level(3);
		return damage;
	}
	@Override public ItemSprite.Glowing glowing() { return GREEN; }
}
