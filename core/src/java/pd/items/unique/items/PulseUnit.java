/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.actors.Char;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;

/**
 * SPSEXPD: 脉冲单元 —— 演员二档（王冠 B 路线）的武器。
 *
 * <p>本该装配在机甲上的东西，靠英雄的电属性魔力驱动；保留钻掘与高攻速，
 * 命中时另外灌注一道_电脉冲_。</p>
 */
public class PulseUnit extends PowerDrill {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.PERFORMER_SHOVEL_T2_2_PULSE;

	/** 电脉冲附加伤害。 */
	public static final int PULSE_BONUS = 5;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(PulseUnit.class)
			.t("name", "脉冲单元")
			.t("ac_drill", "钻掘")
			.t("drill_prompt", "选择相邻的墙壁")
			.t("drill_far", "只能钻相邻的墙壁。")
			.t("drill_not_wall", "那里不是墙壁。")
			.t("drill_progress", "钻头啃进墙面（%d/%d）。")
			.t("drill_done", "墙面在电火花里被熔穿了一块。")
			.t("desc", "按理说这是给机甲装配的，但你的电属性魔力也可以驱动它。\n\n它保留电钻的_高攻速_与「钻掘」——对着_相邻的墙壁_使用，%1$d 次磨开一格。\n\n每次命中还会额外灌进一道_电脉冲_（+%2$d）。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.SOMETHING_0;
		tier = 3;
		unique = true;
	}

	/** SPSEXPD: 高攻速。 */
	@Override public float speedMultiplier(Char owner) {
		return super.speedMultiplier(owner) * 1.7f;
	}

	@Override public int min(int lvl) { return 3 + lvl; }
	@Override public int max(int lvl) { return 8 + 3 * lvl; }

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (defender != null && defender.isAlive()) {
			//SPSEXPD: 电脉冲
			defender.damage(PULSE_BONUS, this);
		}
		return super.proc(attacker, defender, damage);
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
