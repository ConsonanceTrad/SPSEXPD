package pd.items.nornstone;

import pd.atlas.items.SpecificPlaceHolderDict;


public class PurpleNornStone extends NornStone {
	{
		type = 4;
		image = SpecificPlaceHolderDict.SOMETHING_0;
	}

	@Override
	public int value() {
		return 50 * quantity();
	}
}
