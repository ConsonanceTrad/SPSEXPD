/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShadowCurse;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;

import static com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType.DARK_DAMAGE;

public class EnchantmentDark2 extends SpsEnchantment {
	private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0x000000);
	@Override public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		Buff.affect(defender, ShadowCurse.class);
		elementalDamage(weapon, attacker, defender, 0.25f, DARK_DAMAGE);
		if (defender.sprite != null) defender.sprite.emitter().burst(ShadowParticle.UP, 5);
		return damage;
	}
	@Override public ItemSprite.Glowing glowing() { return BLACK; }
}
