package pd.items.specific.challengelists;

import pd.atlas.items.SpecificTaskDict;
import pd.messages.InlineText;
public class WisdomChallenge extends ChallengeList {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WisdomChallenge.class)
			.t("name", "智慧试炼")
			.t("desc", "三大试炼之一。它建立在天空之中，稍有差池就会失去一切。");
	}



	{ image = SpecificTaskDict.WISDOM_CHALLENGE; }
	@Override public int challenge() { return 7; }
}
