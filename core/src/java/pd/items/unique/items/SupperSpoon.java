/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;

/**
 * SPSEXPD: 圣餐银匙 —— 信徒二档（王冠 A 路线）的特殊法杖。
 *
 * <p>与银汤匙同源，但_疲劳上限更高_，可以连续用更久。</p>
 */
public class SupperSpoon extends SilverSpoon {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.FOLLOWER_CUSHION_T2_1_SUPPER;

	/** 疲劳上限。 */
	public static final int FATIGUE_CAP = 160;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(SupperSpoon.class)
			.t("name", "圣餐银匙")
			.t("ac_use", "使用")
			.t("use_prompt", "选择目标（对自己使用可获灵视）")
			.t("no_target", "那里什么也没有。")
			.t("blind", "你尝了尝银匙里的东西……什么都想不起来了。")
			.t("hurt", "疲劳沿着手臂爬上脑门，你眼前一黑（-%d）。")
			.t("desc", "一把更大的银匙，勺底压着一句：可以开饭了吗，我很饿。\n\n它的_充能同样是无限的_，但_疲劳上限比银汤匙高得多_，可以连续用更久。\n\n_对自己_使用可以获得_灵视_；对_敌人_使用则是一道_没有弹道_的直接伤害。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.SOMETHING_0;
		unique = true;
	}

	@Override
	protected int fatigueCap() {
		return FATIGUE_CAP;
	}

	@Override
	protected int spoonDamage() {
		return 4 + level() * 3;
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
