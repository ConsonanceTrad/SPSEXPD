/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.block;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShieldArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.NormalMeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

/** The knuckle-sect weapon sold by Shower after Otiluke is rescued. */
public class SpKnuckles extends NormalMeleeWeapon {

	public SpKnuckles() {
		super(1, 2f, 0.5f, 2, 1, 10, ItemSpriteSheet.SPS_SP_KNUCKLES);
	}

	@Override
	protected void applyLegacyUpgrade(Stats stats) {
		stats.min++;
		stats.max += 2;
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 50) Buff.prolong(defender, Paralysis.class, 2f);
		if (attacker.buff(ShieldArmor.class) == null) {
			Buff.affect(attacker, ShieldArmor.class).level(attacker.HT / 10);
		}
		return super.proc(attacker, defender, damage);
	}
}
