package pd.items.equipment.weapon.melee.normalweapon;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import render.utils.math.Random;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;

public class Lance extends NormalMeleeWeapon {
	{
		image = EquipmentEquipWeaponBasicWeaponDict.LANCE;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Lance.class)
			.t("name", "骑士枪")
			.t("desc", "结实的铁棒连接着巨大圆锥，形成了这么一件武器。——Hmdzl001 \n穿刺");
	}



	public Lance() { super(5, 1f, 1f, 1, 35, 44, SpecificPlaceHolderDict.SOMETHING_0); }
	@Override protected void applyLegacyUpgrade(Stats s) { s.min++; s.max += 3; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(4) == 0) {
			int roll = attackerRoll(attacker);
			defender.damage(safeRandom(roll / 4, roll / 2), this);
		}
		return super.proc(attacker, defender, damage);
	}
}
