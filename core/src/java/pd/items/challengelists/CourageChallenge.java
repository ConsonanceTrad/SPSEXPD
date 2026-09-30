package pd.items.challengelists;
import pd.sprites.ItemSpriteSheet;
public class CourageChallenge extends ChallengeList {
	{ image = ItemSpriteSheet.COURAGE_CHALLENGE; }
	@Override public int challenge() { return 5; }
}
