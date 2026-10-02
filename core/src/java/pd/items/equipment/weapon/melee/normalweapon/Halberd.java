package pd.items.equipment.weapon.melee.normalweapon;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Halberd extends NormalMeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Halberd.class)
			.t("name", "戟斧")
			.t("desc", "长枪和战斧组合而成的武器。——Consideredhamster \n致残");
	}

	public Halberd() { super(5, 1f, 2f, 2, 62, 82, SpecificPlaceHolderDict.SOMETHING_0); }
	@Override protected void applyLegacyUpgrade(Stats s) { if (s.delay > 1.5f) s.delay -= .05f; s.max += 5; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 10) Buff.affect(defender, Cripple.class, 3f);
		return super.proc(attacker, defender, damage);
	}
}
