/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

import static com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType.FIRE_DAMAGE;

public class EnchantmentFire extends SpsEnchantment {
	private static final ItemSprite.Glowing RED = new ItemSprite.Glowing(0xCC0000);
	@Override public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		elementalDamage(weapon, attacker, defender, 0.25f, FIRE_DAMAGE);
		if (Random.Int(4) >= 1) {
			Buff.affect(defender, Burning.class).reignite(defender, 3f);
			if (defender.sprite != null) defender.sprite.emitter().burst(FlameParticle.FACTORY, 5);
		}
		return damage;
	}
	@Override public ItemSprite.Glowing glowing() { return RED; }
}
