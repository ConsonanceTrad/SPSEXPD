/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.armor.specialarmor;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.items.equipment.armor.normalarmor.NormalArmor;
import render.utils.math.Random;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentEquipArmorBasicArmorDict;

public class HuntressArmor extends NormalArmor {
	{
		image = EquipmentEquipArmorBasicArmorDict.HERO_ARMOR_HUNTRESS;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(HuntressArmor.class)
			.t("name", "猎手披风")
			.t("desc", "自然之神给予猎手的祝福，可以利用其力量反击。\n英雄护甲");
	}



	public HuntressArmor() { super(2, 2.4f, 6f, 4, 0, 12, 0, 1, 3, SpecificPlaceHolderDict.SOMETHING_0); }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (attacker != null && Random.Int(8) == 0) attacker.damage(Math.max(0, damage), defender);
		return super.proc(attacker, defender, damage);
	}
}
