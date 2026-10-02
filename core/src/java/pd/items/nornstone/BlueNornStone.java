package pd.items.nornstone;

import pd.atlas.items.SpecificPlaceHolderDict;


public class BlueNornStone extends NornStone {
	{
		type = 2;
		image = SpecificPlaceHolderDict.SOMETHING_0;
	}

	@Override
	public int value() {
		return 50 * quantity();
	}
}
