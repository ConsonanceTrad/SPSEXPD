package pd.items.specific.reward;

import pd.items.Item;
import pd.items.consum.food.fruit.FullMoonberry;
import pd.messages.InlineText;

public class PrisonReward extends ChallengeReward {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PrisonReward.class)
			.t("name", "监狱奖励包")
			.t("desc", "清理奖励，包含一颗满月莓果。")
			.t("ac_use", "使用");
	}

	public PrisonReward() { super(0x0000FF); }
	@Override protected Item[] contents() { return new Item[]{new FullMoonberry()}; }
}
