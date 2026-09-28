/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

/** Threatens ordinary targets and disorients a hero target. */
public class JupitersHorror extends Weapon.Enchantment {
	@Override public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		int level = Math.max(0, weapon.level());
		if (Random.Int(level + 5) >= 4) {
			if (defender == Dungeon.hero) Buff.affect(defender, Vertigo.class, 10f);
			else Buff.affect(defender, Terror.class, Terror.DURATION).object = attacker.id();
		}
		return damage;
	}
	@Override public ItemSprite.Glowing glowing() { return new ItemSprite.Glowing(0x222222); }
}
