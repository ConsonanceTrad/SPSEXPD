/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EarthParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

import static com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType.EARTH_DAMAGE;

public class EnchantmentEarth2 extends SpsEnchantment {
	private static final ItemSprite.Glowing BROWN = new ItemSprite.Glowing(0x996600);
	@Override public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		elementalDamage(weapon, attacker, defender, 0.25f, EARTH_DAMAGE);
		if (Random.Int(4) == 1 && defender.isAlive()) {
			Buff.prolong(defender, Roots.class, 3f);
			Buff.affect(defender, Ooze.class).set(Math.max(0, weapon.level()));
			if (defender.sprite != null) CellEmitter.bottom(defender.pos).start(EarthParticle.FACTORY, 0.05f, 8);
		}
		return damage;
	}
	@Override public ItemSprite.Glowing glowing() { return BROWN; }
}
