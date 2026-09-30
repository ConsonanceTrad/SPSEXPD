/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.melee;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.EnergyArmor;
import pd.actors.buffs.MagicArmor;
import pd.actors.buffs.ShieldArmor;
import pd.actors.buffs.Silent;
import pd.items.weapon.melee.normalweapon.NormalMeleeWeapon;
import pd.sprites.ItemSpriteSheet;

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
