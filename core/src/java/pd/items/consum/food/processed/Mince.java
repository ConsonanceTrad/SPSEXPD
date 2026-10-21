package pd.items.consum.food.processed;

import pd.messages.InlineText;

/** SPSEXPD: 肉馅——丸子/馅饼等肉食的前置中间产物（docs/alchemy-rework.md §7.6）。 */
public class Mince extends Processed {
	static {
		InlineText.of(Mince.class)
			.t("name", "肉馅")
			.t("desc", "剁碎调好的生肉。看上去不是食物，是肉丸、馅饼这类菜的前置材料。");
	}

	@Override public int value() { return 12 * quantity; }
}
