package pd.items.equipment.weapon.melee.normalweapon;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Paralysis;
import render.utils.math.Random;
import pd.messages.InlineText;

public class FightGloves extends NormalMeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(FightGloves.class)
			.t("name", "战斗拳套")
			.t("desc", "简单的皮革加上金属尖块形成了这样一件武器。——Juh9870 \n钝器");
	}



	public FightGloves() { super(2, 1f, 1f, 1, 11, 17, SpecificPlaceHolderDict.SOMETHING_0); }
	@Override protected void applyLegacyUpgrade(Stats s) {
		if (s.accuracy < 1.5f) s.accuracy += .1f;
		if (s.accuracy > 1.45f && s.delay > .55f) s.delay -= .1f;
		if (s.delay < .55f && s.reach < 2) s.reach++;
		s.min++; s.max++;
	}
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 40) Buff.prolong(defender, Paralysis.class, 2f);
		return super.proc(attacker, defender, damage);
	}
}
