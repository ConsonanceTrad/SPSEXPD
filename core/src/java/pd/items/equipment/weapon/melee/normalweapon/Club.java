package pd.items.equipment.weapon.melee.normalweapon;

import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Paralysis;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Club extends NormalMeleeWeapon {
	{
		image = EquipmentEquipWeaponBasicWeaponDict.SPS_WEP_CLUB_0;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Club.class)
			.t("name", "棒槌")
			.t("desc", "一件简单而沉重的木质武器。——Snof33 \n钝器");
	}



	public Club() { super(4, 1f, 1f, 1, 28, 40, EquipmentEquipWeaponBasicWeaponDict.SPS_WEP_CLUB_0); }
	@Override protected void applyLegacyUpgrade(Stats s) { if (s.accuracy < 1.5f) s.accuracy += .05f; s.min += 3; s.max++; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 15) Buff.prolong(defender, Paralysis.class, 2f);
		return super.proc(attacker, defender, damage);
	}
}
