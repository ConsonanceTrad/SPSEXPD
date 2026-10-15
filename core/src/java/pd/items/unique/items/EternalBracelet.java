/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;

/**
 * SPSEXPD: 永夜手镯 —— 盗贼二档（王冠 B 路线）的充能型神器。
 *
 * <p>与暗夜手环同源，但充能上限更高（{@link #CHARGE_CAP}）、蓄能也更快。</p>
 */
public class EternalBracelet extends NightBracelet {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.ROGUE_CLOAK_T2_2_NIGHT;

	/** 充能上限。 */
	public static final int CHARGE_CAP = 20;
	/** 每多少回合恢复 1 点充能（比暗夜手环快得多）。 */
	public static final float TURNS_PER_CHARGE_REGEN = 6f;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(EternalBracelet.class)
			.t("name", "永夜手镯")
			.t("ac_hide", "匿形")
			.t("no_charge", "手镯还没有蓄满夜色。")
			.t("hidden", "浓得化不开的夜色顺着你的手腕漫上来。")
			.t("desc", "一只黑得能吸走光的镯子。\n\n「匿形」：把_全部充能_一次用掉，每 1 点充能换来 _%1$d 回合_隐身。\n\n它蓄能远比暗夜手环快（每 %2$d 回合恢复 1 点，上限 %3$d），也可以用_升级卷轴_继续强化。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.ARTIFACT_HOLDER_0;
	}

	@Override
	protected int chargeCapValue() {
		return CHARGE_CAP;
	}

	@Override
	protected float turnsPerCharge() {
		return TURNS_PER_CHARGE_REGEN;
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
