package pd.items.challengelists;
import pd.sprites.ItemSpriteSheet;
public class PrisonChallenge extends ChallengeList {
	{ image = ItemSpriteSheet.PRISON_CHALLENGE; }
	@Override public int challenge() { return 1; }
}
