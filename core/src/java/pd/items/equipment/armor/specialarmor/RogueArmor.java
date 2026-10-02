/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.armor.specialarmor;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Char;
import pd.items.equipment.armor.normalarmor.NormalArmor;
import render.utils.math.Random;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentEquipArmorBasicArmorDict;

public class RogueArmor extends NormalArmor {
	{
		image = EquipmentEquipArmorBasicArmorDict.HERO_ARMOR_ROGUE;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RogueArmor.class)
			.t("name", "盗贼风衣")
			.t("desc", "来自盗贼自身经验的总结，即使受伤也可以偷窃金币。\n英雄护甲");
	}



	public RogueArmor() { super(1, 5f, 13f, 2, 0, 2, -1, 1, 3, SpecificPlaceHolderDict.SOMETHING_0); }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(8) == 0) Dungeon.gold = Math.max(0, Dungeon.gold + Math.max(0, damage));
		return super.proc(attacker, defender, damage);
	}
}
