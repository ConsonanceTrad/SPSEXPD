/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hot;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Tar;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

import static com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType.FIRE_DAMAGE;

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
