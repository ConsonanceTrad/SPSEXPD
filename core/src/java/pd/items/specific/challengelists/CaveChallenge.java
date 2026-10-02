package pd.items.specific.challengelists;

import pd.atlas.items.SpecificTaskDict;
import pd.messages.InlineText;
public class CaveChallenge extends ChallengeList {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(CaveChallenge.class)
			.t("name", "洞窟挑战");
	}

	{ image = SpecificTaskDict.CAVE_CHALLENGE_0; }
	@Override public int challenge() { return 2; }
}
