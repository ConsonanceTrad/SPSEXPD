package pd.items.equipment.weapon.melee.normalweapon;

import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Spear extends NormalMeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Spear.class)
			.t("name", "长矛")
			.t("desc", "这是一根装着锋锐铁刺的细长木杆。——Watabou \n致残");
	}

	public Spear() { super(2, 1f, 1.5f, 2, 14, 30, EquipmentEquipWeaponBasicWeaponDict.SPS_WEP_SPEAR_0); }
	@Override protected void applyLegacyUpgrade(Stats s) {
		if (s.delay > 1.2f) s.delay -= .05f;
		if (s.delay < 1.2f && s.reach < 3) s.reach++;
		s.min++; s.max += 4;
	}
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 40) Buff.affect(defender, Cripple.class, 3f);
		return super.proc(attacker, defender, damage);
	}
}
