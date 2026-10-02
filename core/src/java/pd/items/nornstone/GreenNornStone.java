package pd.items.nornstone;

import pd.atlas.items.SpecificPlaceHolderDict;


public class GreenNornStone extends NornStone {
	{
		type = 1;
		image = SpecificPlaceHolderDict.SOMETHING_0;
	}

	@Override
	public int value() {
		return 50 * quantity();
	}
}
