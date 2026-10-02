package pd.items.equipment.weapon.melee.normalweapon;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Knuckles extends NormalMeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Knuckles.class)
			.t("name", "指虎")
			.t("desc", "基本上就是带有钢刃的指节套。——Watabou \n致残");
	}

	public Knuckles() { super(1, 1f, 1f, 1, 1, 10, SpecificPlaceHolderDict.SOMETHING_0); }
	@Override protected void applyLegacyUpgrade(Stats s) {
		if (s.delay > .30f) s.delay -= .05f;
		if (s.delay < .35f && s.reach < 2) s.reach++;
		s.min++; s.max++;
	}
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 70) Buff.affect(defender, Cripple.class, 3f);
		return super.proc(attacker, defender, damage);
	}
}
