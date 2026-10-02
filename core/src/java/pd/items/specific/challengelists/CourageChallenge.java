package pd.items.specific.challengelists;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
public class CourageChallenge extends ChallengeList {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(CourageChallenge.class)
			.t("name", "勇气试炼")
			.t("desc", "三大试炼之一。它会带你前往漆黑之地，让你直面原始的恐惧。");
	}



	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override public int challenge() { return 5; }
}
