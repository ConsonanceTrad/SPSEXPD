package pd.items.quest;

import pd.atlas.items.SpecificTaskDict;

import pd.items.Item;

public class GnollClothes extends Item {
	{
		image = SpecificTaskDict.GNOLL_CLOTHES;
		stackable = true;
		unique = true;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}
}
