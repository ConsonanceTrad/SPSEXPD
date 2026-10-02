/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.melee.normalweapon;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.EnergyArmor;
import pd.actors.buffs.MagicArmor;
import pd.actors.buffs.ShieldArmor;
import pd.actors.buffs.Silent;

public class TrickSand extends NormalMeleeWeapon {

	public TrickSand() {
		super(1, 1f, 1f, 2, 1, 10, SpecificPlaceHolderDict.SOMETHING_0);
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
