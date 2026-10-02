package pd.items.nornstone;

import pd.atlas.items.SpecificPlaceHolderDict;


public class YellowNornStone extends NornStone {
	{
		type = 5;
		image = SpecificPlaceHolderDict.SOMETHING_0;
	}

	@Override
	public int value() {
		return 50 * quantity();
	}
}
