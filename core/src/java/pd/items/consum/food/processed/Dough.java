package pd.items.consum.food.processed;

import pd.messages.InlineText;

/** SPSEXPD: 面团——烘焙类食物的前置中间产物（docs/alchemy-rework.md §7.6）。 */
public class Dough extends Processed {
	static {
		InlineText.of(Dough.class)
			.t("name", "面团")
			.t("desc", "主食加水揉成的湿面团，还没上锅。看上去不是食物，是面食的前置材料。");
	}

	@Override public int value() { return 10 * quantity; }
}
