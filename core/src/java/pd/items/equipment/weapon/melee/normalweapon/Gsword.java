package pd.items.equipment.weapon.melee.normalweapon;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Gsword extends NormalMeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Gsword.class)
			.t("name", "巨剑")
			.t("desc", "这把大剑进行的每次沉重挥舞都能造成大量伤害。——00-Evan \n割裂");
	}



	public Gsword() { super(5, 1f, 1f, 1, 50, 64, SpecificPlaceHolderDict.SOMETHING_0); }
	@Override protected void applyLegacyUpgrade(Stats s) { s.min += 3; s.max++; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 10) Buff.affect(defender, Bleeding.class).set(safeRandom(5, damage));
		return super.proc(attacker, defender, damage);
	}
}
