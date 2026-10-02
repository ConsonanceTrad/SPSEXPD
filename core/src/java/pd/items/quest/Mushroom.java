package pd.items.quest;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.items.Item;
import pd.messages.InlineText;

public class Mushroom extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Mushroom.class)
			.t("name", "露珠菌孢")
			.t("desc", "这种罕见菌孢生长在潮湿的地牢中，对等候在入口附近的研究者很有价值。");
	}


	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		unique = true;
	}

	@Override
	public int value() {
		//SPS: 任务蘑菇在 0 层商店固定出售，售价 10 金币（配合开局 10 金币）
		return 10;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}
}
