package pd.items;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentNonEquipDict;


public class UpgradeBlobYellow extends UpgradeBlob {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(UpgradeBlobYellow.class)
			.t("name", "黄色强化精华")
			.t("ac_apply", "使用")
			.t("desc", "吞星花产生的黄色残余物。将其用于物品可提升一个等级。");
	}



	{ image = EquipmentNonEquipDict.YELLOW_UPGRADE_BLOB; }
	@Override protected int upgrades() { return 1; }
}
