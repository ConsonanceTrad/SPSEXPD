package pd.items.specific.challengelists;

import pd.atlas.items.SpecificTaskDict;
import pd.messages.InlineText;
public class PowerChallenge extends ChallengeList {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PowerChallenge.class)
			.t("name", "力量试炼")
			.t("desc", "三大试炼之一。它将测试你的力量，让你在源源不断的大军中存活下来。");
	}



	{ image = SpecificTaskDict.POWER_CHALLENGE; }
	@Override public int challenge() { return 6; }
}
