/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.melee;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.EnergyArmor;
import pd.actors.buffs.MagicArmor;
import pd.actors.buffs.ShieldArmor;
import pd.actors.buffs.Silent;
import pd.items.equipment.weapon.melee.normalweapon.NormalMeleeWeapon;
import pd.messages.InlineText;

public class CurseBox extends NormalMeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(CurseBox.class)
			.t("name", "魔箱")
			.t("desc", "这个家伙喜欢涂满番茄酱的排骨。它能沉默目标，并对魔法护盾造成额外打击。——Coconut");
	}



	public CurseBox() { super(5, 1f, 1f, 2, 40, 50, SpecificPlaceHolderDict.SOMETHING_0); }
	@Override protected void applyLegacyUpgrade(Stats stats) { stats.min++; stats.max++; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (defender.buff(ShieldArmor.class) != null || defender.buff(MagicArmor.class) != null
				|| defender.buff(EnergyArmor.class) != null) defender.damage(Math.max(0, damage), attacker);
		if (defender.buff(Silent.class) != null) defender.damage(Math.max(0, (int)(damage * .5f)), attacker);
		else Buff.affect(defender, Silent.class, 2f);
		return super.proc(attacker, defender, damage);
	}
}
