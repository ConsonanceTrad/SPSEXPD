/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.actors.Char;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;

/**
 * SPSEXPD: 神圣钻头 —— 演员二档（王冠 B 路线）的武器。
 *
 * <p>电钻的强化档：钻得更快、更狠，磨开墙面需要的次数也更少。</p>
 */
public class HolyDrill extends PowerDrill {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.PERFORMER_SHOVEL_T2_2_HOLY;

	/** 磨开一格墙需要的次数（比电钻少）。 */
	public static final int DIGS = 4;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(HolyDrill.class)
			.t("name", "神圣钻头")
			.t("ac_drill", "钻掘")
			.t("drill_prompt", "选择相邻的墙壁")
			.t("drill_far", "只能钻相邻的墙壁。")
			.t("drill_not_wall", "那里不是墙壁。")
			.t("drill_progress", "钻头啃进墙面（%d/%d）。")
			.t("drill_done", "墙面在一声闷响里塌出了洞。")
			.t("desc", "最不合时宜的挖掘道具——它本该待在某座神殿的陈列柜里。\n\n它钻得比电钻更快，_攻击速度同样远高于同类武器_。\n\n「钻掘」：对着_相邻的墙壁_使用，只要 %1$d 次就能磨开一格。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.SOMETHING_0;
		tier = 3;
		unique = true;
	}

	/** SPSEXPD: 高攻速（比电钻更快）。 */
	@Override public float speedMultiplier(Char owner) {
		return super.speedMultiplier(owner) * 1.9f;
	}

	@Override public int min(int lvl) { return 3 + lvl; }
	@Override public int max(int lvl) { return 9 + 3 * lvl; }

	@Override
	protected int digsRequired() {
		return DIGS;
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
