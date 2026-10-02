package pd.items;

import pd.atlas.items.ConsumGoodsMaterialsMaterialsDict;
public class AdamantWeapon extends Item {
	{ image = ConsumGoodsMaterialsMaterialsDict.WEAPON_WELD_PART; unique = true; }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 250 * quantity; }
}
