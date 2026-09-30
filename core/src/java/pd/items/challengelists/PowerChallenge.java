package pd.items.challengelists;
import pd.sprites.ItemSpriteSheet;
public class PowerChallenge extends ChallengeList {
	{ image = ItemSpriteSheet.POWER_CHALLENGE; }
	@Override public int challenge() { return 6; }
}
