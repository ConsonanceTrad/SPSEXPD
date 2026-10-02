package pd.items.nornstone;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;


public class OrangeNornStone extends NornStone {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(OrangeNornStone.class)
			.t("name", "橙色魔法矿石")
			.t("desc", "多利亚哈芬的特产，富有能量的魔法矿石。两块以上可在祭坛祝圣为_碎骨锯肉巨斧_。");
	}

	{
		type = 3;
		image = SpecificPlaceHolderDict.SOMETHING_0;
	}

	@Override
	public int value() {
		return 50 * quantity();
	}
}
