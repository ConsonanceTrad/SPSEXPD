package pd.items;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;


public class UpgradeBlobRed extends UpgradeBlob {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(UpgradeBlobRed.class)
			.t("name", "红色强化精华")
			.t("ac_apply", "使用")
			.t("desc", "吞星花产生的红色残余物。将其用于物品可提升三个等级。");
	}



	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override protected int upgrades() { return 3; }
}
