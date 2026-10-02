package pd.items.specific.challengelists;

import pd.atlas.items.SpecificTaskDict;
import pd.messages.InlineText;
public class SewerChallenge extends ChallengeList {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(SewerChallenge.class)
			.t("name", "下水道挑战");
	}



	{ image = SpecificTaskDict.SEWER_CHALLENGE_0; }
	@Override public int challenge() { return 0; }
}
