package pd.items.nornstone;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.items.Item;
import pd.messages.InlineText;

public class NornStone extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(NornStone.class)
			.t("name", "魔法矿石")
			.t("desc", "多利亚哈芬的特产，富有能量的魔法矿石。");
	}




	public int type;

	{
		stackable = true;
		image = SpecificPlaceHolderDict.SOMETHING_0;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public int value() {
		return 100 * quantity();
	}
}
