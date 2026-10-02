package pd.items.specific.challengelists;

import pd.atlas.items.SpecificTaskDict;
import pd.messages.InlineText;
public class PrisonChallenge extends ChallengeList {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PrisonChallenge.class)
			.t("name", "监狱挑战");
	}

	{ image = SpecificTaskDict.PRISON_CHALLENGE; }
	@Override public int challenge() { return 1; }
}
