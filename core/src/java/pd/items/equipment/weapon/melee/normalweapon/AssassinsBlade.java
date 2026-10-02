package pd.items.equipment.weapon.melee.normalweapon;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import render.utils.math.Random;
import pd.messages.InlineText;

public class AssassinsBlade extends NormalMeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(AssassinsBlade.class)
			.t("name", "暗杀之刃")
			.t("desc", "黑曜石制的波浪形短刃，虽轻便但不易用，如果能击中要害足以致命。——00-Evan \n穿刺");
	}



	public AssassinsBlade() { super(4, 1f, 1f, 1, 26, 34, SpecificPlaceHolderDict.SOMETHING_0); }
	@Override protected void applyLegacyUpgrade(Stats s) { s.min += 3; s.max++; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 50) {
			int roll = attackerRoll(attacker);
			defender.damage(safeRandom(roll / 4, roll / 2), this);
		}
		return super.proc(attacker, defender, damage);
	}
}
