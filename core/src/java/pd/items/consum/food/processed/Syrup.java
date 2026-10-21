package pd.items.consum.food.processed;

import pd.messages.InlineText;

/** SPSEXPD: 甜酱——蜂蜜调成的甜品辅料（docs/alchemy-rework.md §7.6）。 */
public class Syrup extends Processed {
	static {
		InlineText.of(Syrup.class)
			.t("name", "甜酱")
			.t("desc", "蜂蜜兑水熬成的稠酱，甜得发腻。看上去不是食物，是甜品的前置材料。");
	}

	@Override public int value() { return 14 * quantity; }
}
