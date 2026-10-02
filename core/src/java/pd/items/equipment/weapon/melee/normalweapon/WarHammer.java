package pd.items.equipment.weapon.melee.normalweapon;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Paralysis;
import render.utils.math.Random;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;

public class WarHammer extends NormalMeleeWeapon {
	{
		image = EquipmentEquipWeaponBasicWeaponDict.ENCHANTED_AXE_BLADE;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WarHammer.class)
			.t("name", "战锤")
			.t("desc", "很少有生物能抵挡这铅铁巨块的辗压，但也只有最强壮的冒险者才能有效使用它。——Watabou \n钝器");
	}



	public WarHammer() { super(5, 1f, 1f, 1, 41, 56, SpecificPlaceHolderDict.SOMETHING_0); }
	@Override protected void applyLegacyUpgrade(Stats s) { if (s.accuracy < 2f) s.accuracy += .1f; s.min++; s.max += 3; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 10) Buff.prolong(defender, Paralysis.class, 2f);
		return super.proc(attacker, defender, damage);
	}
}
