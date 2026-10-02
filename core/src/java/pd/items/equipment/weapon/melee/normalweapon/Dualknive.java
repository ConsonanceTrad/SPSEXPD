package pd.items.equipment.weapon.melee.normalweapon;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.messages.InlineText;

public class Dualknive extends NormalMeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Dualknive.class)
			.t("name", "对剑")
			.t("desc", "成对的刀片带来更高的伤害。——Bilboldev \n穿刺");
	}



	public Dualknive() { super(2, 1f, 1f, 1, 11, 17, SpecificPlaceHolderDict.SOMETHING_0); }
	@Override protected void applyLegacyUpgrade(Stats s) {
		if (s.accuracy < 1.2f) s.accuracy += .05f;
		if (s.accuracy > 1.2f && s.delay > .8f) s.delay -= .05f;
		if (s.delay < .8f && s.reach < 2) s.reach++;
		s.min++; s.max++;
	}
	@Override public int proc(Char attacker, Char defender, int damage) {
		int roll = attackerRoll(attacker);
		defender.damage(safeRandom(roll / 4, roll / 2), this);
		return super.proc(attacker, defender, damage);
	}
}
