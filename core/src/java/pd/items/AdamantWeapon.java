package pd.items;

import pd.atlas.items.ConsumGoodsMaterialsMaterialsDict;
import pd.messages.InlineText;
public class AdamantWeapon extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(AdamantWeapon.class)
			.t("name", "焊接组件-武器")
			.t("desc", "用于焊接武器的组件。");
	}

	{ image = ConsumGoodsMaterialsMaterialsDict.WEAPON_WELD_PART; unique = true; }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 250 * quantity; }
}
