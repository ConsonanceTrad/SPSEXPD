package pd.items.challengelists;

import pd.atlas.items.SpecificTaskDict;
public class PrisonChallenge extends ChallengeList {
	{ image = SpecificTaskDict.PRISON_CHALLENGE; }
	@Override public int challenge() { return 1; }
}
