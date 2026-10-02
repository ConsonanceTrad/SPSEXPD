package pd.items;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
public class AdamantArmor extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(AdamantArmor.class)
			.t("name", "焊接组件-护甲")
			.t("desc", "用于焊接护甲的组件。");
	}

	{ image = SpecificPlaceHolderDict.SOMETHING_0; unique = true; }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 300 * quantity; }
}
