package pd.items.specific.reward;

import pd.items.Item;
import pd.items.consum.food.fruit.Blackberry;
import pd.items.consum.food.fruit.Blueberry;
import pd.items.consum.food.fruit.Cloudberry;
import pd.items.consum.food.fruit.Moonberry;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentBagsDict;

public class CaveReward extends ChallengeReward {
	{
		image = EquipmentBagsDict.BACKPACK_0;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(CaveReward.class)
			.t("name", "洞穴奖励包")
			.t("desc", "清理奖励，包含四种莓果各10颗。")
			.t("ac_use", "使用");
	}



	public CaveReward() { super(0xFFFFFF); }
	@Override protected Item[] contents() {
		return new Item[]{new Moonberry().quantity(10), new Cloudberry().quantity(10),
				new Blueberry().quantity(10), new Blackberry().quantity(10)};
	}
}
