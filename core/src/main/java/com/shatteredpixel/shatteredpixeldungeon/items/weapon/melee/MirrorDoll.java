/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.EnergyArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShieldArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silent;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.NormalMeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class MirrorDoll extends NormalMeleeWeapon {
	public MirrorDoll() { super(2, 1f, 1f, 2, 12, 17, ItemSpriteSheet.SPS_MIRROR_DOLL); }
	@Override protected void applyLegacyUpgrade(Stats stats) { stats.min++; stats.max++; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (defender.buff(ShieldArmor.class) != null || defender.buff(MagicArmor.class) != null
				|| defender.buff(EnergyArmor.class) != null) defender.damage(Math.max(0, damage), attacker);
		if (defender.buff(Silent.class) != null) defender.damage(Math.max(0, (int)(damage * .5f)), attacker);
		else Buff.affect(defender, Silent.class, 5f);
		return super.proc(attacker, defender, damage);
	}
}
