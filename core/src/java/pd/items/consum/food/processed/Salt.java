package pd.items.consum.food.processed;

import pd.messages.InlineText;

/** SPSEXPD: 盐——原石研磨出的调味料，腌制与烹饪的辅料（docs/alchemy-rework.md §7.6）。 */
public class Salt extends Processed {
	static {
		InlineText.of(Salt.class)
			.t("name", "盐")
			.t("desc", "从原石里磨出来的粗盐，颗粒发灰。看上去不是食物，但腌肉、调味都离不开它。");
	}

	@Override public int value() { return 8 * quantity; }
}
