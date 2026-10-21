package pd.items.consum.food.processed;

import pd.messages.InlineText;

/** SPSEXPD: 腌肉——盐渍的肉，可长期保存（docs/alchemy-rework.md §7.6）。 */
public class PickledMeat extends Processed {
	static {
		InlineText.of(PickledMeat.class)
			.t("name", "腌肉")
			.t("desc", "用盐腌透的肉块，能放很久。看上去不是食物，是耐储存的肉食材料。");
	}

	@Override public int value() { return 20 * quantity; }
}
