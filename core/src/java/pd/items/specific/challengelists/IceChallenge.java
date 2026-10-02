package pd.items.specific.challengelists;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
public class IceChallenge extends ChallengeList {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(IceChallenge.class)
			.t("name", "蜜雪冰城");
	}



	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override public int challenge() { return 4; }
}
