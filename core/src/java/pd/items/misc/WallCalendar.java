/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.Statistics;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.Item;
import pd.messages.InlineText;
import pd.messages.Messages;

/**
 * SPSEXPD: 挂历——查看当前的游戏内日期（年 / 月 / 日）。
 *
 * <p>日期来自 {@link Statistics} 的日历基准：开局随机一个起始日期，之后每过一个游戏日推进一天，
 * 月与日按 30 天/月、12 月/年等比换算（见 {@code Statistics.calendarYear/Month/Day()}）。
 * 图标暂用炼金材料同款占位图，后续可换专用图。</p>
 */
public class WallCalendar extends Item {
	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(WallCalendar.class)
			.t("name", "挂历")
			.t("desc", "一本挂在墙上的旧日历，纸页已经泛黄。翻开来就能看到今天是哪一天。")
			.t("today", "今天是 %1$d 年 %2$d 月 %3$d 日。");
	}

	{
		image = SpecificPlaceHolderDict.SPS_PH_ALCHEMY;
		stackable = false;
	}

	@Override
	public String info() {
		//SPSEXPD: 日期是动态的，所以放在 info() 而不是静态 desc 里
		return Messages.get(WallCalendar.class, "desc")
				+ "\n\n" + Messages.get(WallCalendar.class, "today",
						Statistics.calendarYear(), Statistics.calendarMonth(), Statistics.calendarDay());
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 20 * quantity; }
}
