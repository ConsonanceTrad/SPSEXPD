/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.EnergyArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShieldArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silent;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class TrickSand extends NormalMeleeWeapon {

	public TrickSand() {
		super(1, 1f, 1f, 2, 1, 10, ItemSpriteSheet.LEGACY_TRICK_SAND);
	}

	@Override
	protected void applyLegacyUpgrade(Stats stats) {
		stats.min++;
		stats.max++;
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (defender.buff(ShieldArmor.class) != null || defender.buff(MagicArmor.class) != null
				|| defender.buff(EnergyArmor.class) != null) {
			defender.damage(damage, attacker);
		}
		if (defender.buff(Silent.class) != null) defender.damage((int)(damage * .5f), attacker);
		else Buff.affect(defender, Silent.class, 6f);
		return super.proc(attacker, defender, damage);
	}
}
