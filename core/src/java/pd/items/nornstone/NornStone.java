package pd.items.nornstone;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.items.Item;

public class NornStone extends Item {

	public int type;

	{
		stackable = true;
		image = SpecificPlaceHolderDict.SOMETHING_0;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public int value() {
		return 100 * quantity();
	}
}
