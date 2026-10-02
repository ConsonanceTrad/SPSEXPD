package pd.items.specific.reward;

import pd.items.Crystalnucleus;
import pd.items.Item;
import pd.messages.InlineText;

public class CityReward extends ChallengeReward {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(CityReward.class)
			.t("name", "城市奖励包")
			.t("desc", "清理奖励，包含5枚价值1000金币的闪耀晶核。")
			.t("ac_use", "使用");
	}



	public CityReward() { super(0xFFFF44); }
	@Override protected Item[] contents() {
		return new Item[]{new Crystalnucleus(), new Crystalnucleus(), new Crystalnucleus(),
				new Crystalnucleus(), new Crystalnucleus()};
	}
}
