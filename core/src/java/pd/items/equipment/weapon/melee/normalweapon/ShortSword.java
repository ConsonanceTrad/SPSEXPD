package pd.items.equipment.weapon.melee.normalweapon;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import render.utils.math.Random;
import pd.messages.InlineText;

public class ShortSword extends NormalMeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ShortSword.class)
			.t("name", "短剑")
			.t("desc", "它确实相当短，不比一把匕首长出几英寸。——Watabou \n割裂");
	}



	public ShortSword() { super(1, 1f, 1f, 1, 1, 10, SpecificPlaceHolderDict.SOMETHING_0); }
	@Override protected void applyLegacyUpgrade(Stats s) { s.min += 3; s.max += 3; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 50) Buff.affect(defender, Bleeding.class).set(safeRandom(1, damage));
		return super.proc(attacker, defender, damage);
	}
}
