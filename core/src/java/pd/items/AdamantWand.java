package pd.items;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
import pd.atlas.items.ConsumGoodsMaterialsMaterialsDict;
public class AdamantWand extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(AdamantWand.class)
			.t("name", "焊接组件-法杖")
			.t("desc", "用于焊接法杖的组件。");
	}



	{ image = ConsumGoodsMaterialsMaterialsDict.WAND_WELD_PART; unique = true; }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 150 * quantity; }
}
