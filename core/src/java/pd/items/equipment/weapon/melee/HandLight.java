/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.melee;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.EnergyArmor;
import pd.actors.buffs.MagicArmor;
import pd.actors.buffs.ShieldArmor;
import pd.actors.buffs.Silent;
import pd.items.equipment.weapon.melee.normalweapon.NormalMeleeWeapon;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;

public class HandLight extends NormalMeleeWeapon {
	{
		image = EquipmentEquipWeaponBasicWeaponDict.LAMP_PUNCH;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(HandLight.class)
			.t("name", "神拳灯")
			.t("desc", "挖出来，擦一下，数个三，上勾拳。它能沉默目标，并对魔法护盾造成额外打击。——Coconut");
	}



	public HandLight() { super(4, 1f, 1f, 2, 29, 38, SpecificPlaceHolderDict.SOMETHING_0); }
	@Override protected void applyLegacyUpgrade(Stats stats) { stats.min++; stats.max++; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (defender.buff(ShieldArmor.class) != null || defender.buff(MagicArmor.class) != null
				|| defender.buff(EnergyArmor.class) != null) defender.damage(Math.max(0, damage), attacker);
		if (defender.buff(Silent.class) != null) defender.damage(Math.max(0, (int)(damage * .5f)), attacker);
		else Buff.affect(defender, Silent.class, 3f);
		return super.proc(attacker, defender, damage);
	}
}
