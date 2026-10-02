/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.armor.specialarmor;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.items.equipment.armor.normalarmor.NormalArmor;
import render.utils.math.Random;
import pd.messages.InlineText;

public class MageArmor extends NormalArmor {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(MageArmor.class)
			.t("name", "法师长袍")
			.t("desc", "基于法师对时间的研究，有几率免疫受到的伤害。\n英雄护甲");
	}

	public MageArmor() { super(1, 3f, 7f, 4, 0, 4, 0, 1, 4, SpecificPlaceHolderDict.SOMETHING_0); }
	@Override public int proc(Char attacker, Char defender, int damage) {
		return super.proc(attacker, defender, Random.Int(8) == 0 ? 0 : damage);
	}
}
