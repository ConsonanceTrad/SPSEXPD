package pd.items.challengelists;
import pd.sprites.ItemSpriteSheet;
public class CityChallenge extends ChallengeList {
	{ image = ItemSpriteSheet.CITY_CHALLENGE; }
	@Override public int challenge() { return 3; }
}
