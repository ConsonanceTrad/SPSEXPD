package pd.items.quest;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.items.Item;

public class Mushroom extends Item {

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		unique = true;
	}

	@Override
	public int value() {
		//SPS: 任务蘑菇在 0 层商店固定出售，售价 10 金币（配合开局 10 金币）
		return 10;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}
}
