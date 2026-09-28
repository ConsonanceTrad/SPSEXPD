/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FrostIce;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SnowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

import static com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType.ICE_DAMAGE;

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
