package pd.items.nornstone;

import pd.atlas.items.SpecificPlaceHolderDict;


public class OrangeNornStone extends NornStone {
	{
		type = 3;
		image = SpecificPlaceHolderDict.SOMETHING_0;
	}

	@Override
	public int value() {
		return 50 * quantity();
	}
}
