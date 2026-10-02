package pd.items.equipment.weapon.melee.normalweapon;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;

public class Dagger extends NormalMeleeWeapon {
	{
		image = EquipmentEquipWeaponBasicWeaponDict.DAGGER_0;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Dagger.class)
			.t("name", "匕首")
			.t("desc", "配以磨损木柄的简单钢匕首。——Watabou \n穿刺");
	}



	public Dagger() { super(1, 1f, 1f, 1, 1, 10, SpecificPlaceHolderDict.SOMETHING_0); }
	@Override protected void applyLegacyUpgrade(Stats s) { if (s.accuracy < 4f) s.accuracy += .2f; s.min++; s.max++; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		int roll = attackerRoll(attacker);
		defender.damage(safeRandom(roll / 2, roll * 3 / 4), this);
		return super.proc(attacker, defender, damage);
	}
}
