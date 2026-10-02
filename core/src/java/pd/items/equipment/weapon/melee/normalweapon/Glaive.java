package pd.items.equipment.weapon.melee.normalweapon;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Glaive extends NormalMeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Glaive.class)
			.t("name", "关刀")
			.t("desc", "由一支长木杆和末端接上的利刃组成。——Watabou \n致残");
	}



	public Glaive() { super(4, 1f, 1.75f, 2, 42, 60, SpecificPlaceHolderDict.SOMETHING_0); }
	@Override protected void applyLegacyUpgrade(Stats s) { if (s.delay > 1.4f) s.delay -= .05f; s.min++; s.max += 6; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 20) Buff.affect(defender, Cripple.class, 3f);
		return super.proc(attacker, defender, damage);
	}
}
