/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.melee.start;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Paralysis;
import pd.items.equipment.weapon.melee.normalweapon.NormalMeleeWeapon;
import render.utils.math.Random;
import pd.messages.InlineText;

public class BunnySpanner extends NormalMeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(BunnySpanner.class)
			.t("name", "兔人扳手")
			.t("desc", "兔人战士使用的巨大扳手。每次命中有30%%概率使目标麻痹。");
	}



	public BunnySpanner() {
		super(1, 1.2f, 1.5f, 2, 8, 15, SpecificPlaceHolderDict.SOMETHING_0);
		unique = true;
		reinforced = true;
		cursed = true;
	}
	@Override protected void applyLegacyUpgrade(Stats stats) { stats.min += 2; stats.max += 3; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 30) Buff.prolong(defender, Paralysis.class, 2f);
		return super.proc(attacker, defender, damage);
	}
}
