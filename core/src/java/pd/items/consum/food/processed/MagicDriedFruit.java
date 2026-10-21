package pd.items.consum.food.processed;

import pd.messages.InlineText;

/**
 * SPSEXPD: 魔力果干——任意大型果实烘干制成的稀有食材（docs/alchemy-rework.md §5.4）。
 * 不分大果类型，可卖出好价钱，也可作为高档烹饪原料。
 */
public class MagicDriedFruit extends Processed {
	static {
		InlineText.of(MagicDriedFruit.class)
			.t("name", "魔力果干")
			.t("desc", "大型果实烘干后留下的果干，甜味被浓缩成一层薄薄的糖霜。"
					+ "看上去不是食物——它是值钱的高档食材，能卖个好价钱。");
	}

	@Override public int value() { return 100 * quantity; }
}
