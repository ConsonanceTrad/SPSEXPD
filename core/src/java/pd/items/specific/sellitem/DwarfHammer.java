package pd.items.specific.sellitem;

import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;
import pd.messages.InlineText;
public class DwarfHammer extends SellItem {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(DwarfHammer.class)
			.t("name", "破魔之锤")
			.t("desc", "矮人族在战胜古神后，用科学魔法将古神封印在地牢深处。这柄锤子已经太脆弱，不能再当武器使用。");
	}



	{ image = EquipmentEquipWeaponBasicWeaponDict.DWARF_HAMMER; }
	@Override public int value() { return 150 * quantity; }
}
