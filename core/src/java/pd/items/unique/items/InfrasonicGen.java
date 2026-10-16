/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Paralysis;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.equipment.weapon.melee.MeleeWeapon;
import pd.items.unique.UniqueItem;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;

/**
 * SPSEXPD: 次声波发生器 —— 演员一档（面具 B 路线）的武器。
 *
 * <p>命中时用一记次声波把目标_单点麻痹_。</p>
 */
public class InfrasonicGen extends MeleeWeapon implements UniqueItem {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.PERFORMER_SHOVEL_T1_2_INFRA;

	/** 麻痹回合数。 */
	public static final float PARALYSIS_TURNS = 1f;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(InfrasonicGen.class)
			.t("name", "次声波发生器")
			.t("desc", "一台方头方脑的机器，按下开关时你会先觉得耳朵发闷。\n\n每次命中都会对目标放出一记次声波，让它_单点麻痹_ %1$d 回合。\n\n它差点杀死了一个英雄。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.SOMETHING_0;
		tier = 2;
		unique = true;
	}

	@Override public int min(int lvl) { return 3 + lvl; }
	@Override public int max(int lvl) { return 7 + 2 * lvl; }
	@Override public int STRReq(int lvl) { return 10; }

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (defender != null && defender.isAlive()) {
			Buff.prolong(defender, Paralysis.class, PARALYSIS_TURNS);
		}
		return super.proc(attacker, defender, damage);
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
