package pd.items.specific.sellitem;

import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;
public class DwarfHammer extends SellItem {
	{ image = EquipmentEquipWeaponBasicWeaponDict.DWARF_HAMMER; }
	@Override public int value() { return 150 * quantity; }
}
