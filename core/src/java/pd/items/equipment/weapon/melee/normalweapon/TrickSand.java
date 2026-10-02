/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.melee.normalweapon;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.EnergyArmor;
import pd.actors.buffs.MagicArmor;
import pd.actors.buffs.ShieldArmor;
import pd.actors.buffs.Silent;
import pd.messages.InlineText;

public class TrickSand extends NormalMeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(TrickSand.class)
			.t("name", "诡异沙尘")
			.t("desc", "价值90金币的沙尘，也许可以用来揭示隐藏单位。它能攻击两格外的目标，使其沉默，并对魔法护盾造成额外打击。");
	}


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
