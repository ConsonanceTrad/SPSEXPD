/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.armor.specialarmor;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.items.equipment.armor.normalarmor.NormalArmor;
import render.utils.math.Random;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentEquipArmorBasicArmorDict;

public class FollowerArmor extends NormalArmor {
	{
		image = EquipmentEquipArmorBasicArmorDict.HERO_ARMOR_CLERIC;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(FollowerArmor.class)
			.t("name", "信徒外套")
			.t("desc", "看上去普通的信徒服装，能够从他人身上汲取力量。\n英雄护甲");
	}



	public FollowerArmor() { super(4, 3.5f, 10f, 5, 0, 20, -1, 1, 3, SpecificPlaceHolderDict.SOMETHING_0); }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(8) == 0) defender.HP = Math.min(defender.HT, defender.HP + Math.max(0, damage / 4));
		return super.proc(attacker, defender, damage);
	}
}
