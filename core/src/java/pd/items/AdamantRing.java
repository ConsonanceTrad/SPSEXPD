package pd.items;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
import pd.atlas.items.ConsumGoodsMaterialsMaterialsDict;
public class AdamantRing extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(AdamantRing.class)
			.t("name", "焊接组件-戒指")
			.t("desc", "用于焊接戒指的组件。");
	}



	{ image = ConsumGoodsMaterialsMaterialsDict.RING_WELD_PART; unique = true; }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 200 * quantity; }
}
