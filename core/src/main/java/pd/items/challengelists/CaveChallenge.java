package pd.items.challengelists;
import pd.sprites.ItemSpriteSheet;
public class CaveChallenge extends ChallengeList {
	{ image = ItemSpriteSheet.CAVE_CHALLENGE; }
	@Override public int challenge() { return 2; }
}
