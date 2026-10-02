package pd.items;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;


public class UpgradeBlobViolet extends UpgradeBlob {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(UpgradeBlobViolet.class)
			.t("name", "紫色强化精华")
			.t("ac_apply", "使用")
			.t("desc", "吞星花产生的罕见紫色残余物。将其用于物品可提升五个等级。");
	}



	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override protected int upgrades() { return 5; }
}
