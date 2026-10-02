package pd.items.equipment.weapon.melee.normalweapon;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.HolyStun;
import render.utils.math.Random;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;

public class Nunchakus extends NormalMeleeWeapon {
	{
		image = EquipmentEquipWeaponBasicWeaponDict.NUNCHAKU;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Nunchakus.class)
			.t("name", "双截棍")
			.t("desc", "两根粗短的木棍被结实的绳索连接，形成了这么一件武器。——Hmdzl001 \n高级钝器");
	}



	public Nunchakus() { super(3, 1f, 1f, 1, 18, 27, SpecificPlaceHolderDict.SOMETHING_0); }
	@Override protected void applyLegacyUpgrade(Stats s) { if (s.delay > .75f) s.delay -= .05f; s.min += 2; s.max++; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 20) Buff.prolong(defender, HolyStun.class, 2f);
		return super.proc(attacker, defender, damage);
	}
}
