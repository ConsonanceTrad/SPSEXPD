/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.armor.specialarmor;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Charm;
import pd.items.equipment.armor.normalarmor.NormalArmor;
import render.utils.math.Random;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentEquipArmorBasicArmorDict;

public class PerformerArmor extends NormalArmor {
	{
		image = EquipmentEquipArmorBasicArmorDict.HERO_ARMOR_PERFORMER;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PerformerArmor.class)
			.t("name", "演员夹克")
			.t("desc", "大受欢迎的演员套装，能够迷倒万千粉丝。\n英雄护甲");
	}



	public PerformerArmor() { super(2, 4f, 12f, 3, 0, 10, -1, 1, 3, SpecificPlaceHolderDict.SOMETHING_0); }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (attacker != null && Random.Int(8) == 0) Buff.affect(attacker, Charm.class, 3f).object = defender.id();
		return super.proc(attacker, defender, damage);
	}
}
