package pd.items.specific.reward;

import pd.items.Item;
import pd.items.StoneOre;
import pd.messages.InlineText;

public class SewerReward extends ChallengeReward {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(SewerReward.class)
			.t("name", "下水道奖励包")
			.t("desc", "清理奖励，包含20枚原石。")
			.t("ac_use", "使用");
	}

	public SewerReward() { super(0x000000); }
	@Override protected Item[] contents() { return new Item[]{new StoneOre(20)}; }
}
