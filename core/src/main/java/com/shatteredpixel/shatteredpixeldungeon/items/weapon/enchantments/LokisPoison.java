/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

/** Applies the stronger SPS relic poison. */
public class LokisPoison extends Weapon.Enchantment {
	@Override public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		int level = Math.max(0, weapon.level());
		if (Random.Int(level + 3) >= 2) {
			Buff.affect(defender, com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LokisPoison.class)
					.set(level + 1f);
		}
		return damage;
	}
	@Override public ItemSprite.Glowing glowing() { return new ItemSprite.Glowing(0x4400AA); }
}
