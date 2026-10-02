/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.melee.special;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Terror;
import pd.actors.buffs.Vertigo;
import render.utils.math.Random;
import pd.messages.InlineText;
import pd.atlas.items.ConsumThrowsDict;

public class WraithBreath extends SpsSpecialMeleeWeapon {
	{
		image = ConsumThrowsDict.SONIC_BAIT;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WraithBreath.class)
			.t("name", "幽灵之息")
			.t("desc", "来自幽灵的气息。\n恐吓");
	}



	public WraithBreath() { super(2, .75f, 1f, 4, 7, 11, SpecificPlaceHolderDict.SOMETHING_0); }
	@Override public int min(int level) { return 7 + Math.max(0, level) * 2; }
	@Override public int max(int level) { return 11 + Math.max(0, level) * 3; }

	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 50) {
			Buff.affect(defender, Vertigo.class, 10f);
			Buff.affect(defender, Terror.class, Terror.DURATION).object = attacker.id();
		}
		return super.proc(attacker, defender, damage);
	}
}
