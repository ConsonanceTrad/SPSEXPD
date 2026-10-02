package pd.items.specific.challengelists;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
public class CityChallenge extends ChallengeList {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(CityChallenge.class)
			.t("name", "城市挑战");
	}

	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override public int challenge() { return 3; }
}
