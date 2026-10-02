package pd.items.nornstone;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
import pd.atlas.items.ConsumGoodsMaterialsMaterialsDict;


public class GreenNornStone extends NornStone {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(GreenNornStone.class)
			.t("name", "绿色魔法矿石")
			.t("desc", "多利亚哈芬的特产，富有能量的魔法矿石。两块以上可在祭坛祝圣为_猛毒重型链枷_。");
	}



	{
		type = 1;
		image = ConsumGoodsMaterialsMaterialsDict.GREEN_NORN_ORE;
	}

	@Override
	public int value() {
		return 50 * quantity();
	}
}
