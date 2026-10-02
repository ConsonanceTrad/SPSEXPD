/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.meleethrow;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Charm;
import render.utils.math.Random;
import pd.messages.InlineText;

public class MiniMoai extends MeleeThrowWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(MiniMoai.class)
			.t("name", "迷你复活节岛石雕")
			.t("desc", "一个旅游纪念品，就当是复活节的彩蛋吧。");
	}



	public MiniMoai() { super(1, 10, 10, SpecificPlaceHolderDict.SOMETHING_0); }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(10) > 7) Buff.prolong(defender, Charm.class, 3f).object = attacker.id();
		return super.proc(attacker, defender, damage);
	}
	@Override public int value() { return 100; }
}
