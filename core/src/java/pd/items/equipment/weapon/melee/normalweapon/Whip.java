package pd.items.equipment.weapon.melee.normalweapon;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Roots;
import render.utils.math.Random;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;

public class Whip extends NormalMeleeWeapon {
	{
		image = EquipmentEquipWeaponBasicWeaponDict.WHIP_0;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Whip.class)
			.t("name", "长鞭")
			.t("desc", "虽然这把武器另一端带倒刺的绳子伤害不高，但它的攻击范围是数一数二的。——00-Evan \n高级致残");
	}



	public Whip() { super(3, 1f, 1f, 2, 24, 35, SpecificPlaceHolderDict.SOMETHING_0); }
	@Override protected void applyLegacyUpgrade(Stats s) {
		if (s.accuracy < 1.3f) s.accuracy += .05f;
		if (s.reach < 3 && s.accuracy > 1.3f) s.reach++;
		s.min++; s.max += 2;
	}
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 30) Buff.affect(defender, Roots.class, 1f);
		return super.proc(attacker, defender, damage);
	}
}
