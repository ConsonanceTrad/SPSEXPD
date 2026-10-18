/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;

/**
 * SPSEXPD: 脉冲单元 —— 次声波发生器的二档升级版，同样是<b>特殊物品</b>而不是武器。
 *
 * <p>与发生器完全同型（充能、施放、视野内目标、无弹道），只是麻痹更久。</p>
 */
public class PulseUnit extends InfrasonicGen {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.PERFORMER_SHOVEL_T2_2_PULSE;

	/** 麻痹回合数。 */
	public static final int PARALYSIS_TURNS = 2;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(PulseUnit.class)
			.t("name", "脉冲单元")
			.t("desc", "按理说这是给机甲装配的，但你的电属性魔力也可以驱动它。\n\n它比发生器的声波更狠：按「施放」对准_视野内的目标_灌进一道电脉冲，让其_麻痹_ %1$d 回合。\n\n充能会随时间自行恢复。");
	}

	@Override
	public int paralysisTurns() {
		return PARALYSIS_TURNS;
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
