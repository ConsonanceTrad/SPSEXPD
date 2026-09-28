package com.shatteredpixel.shatteredpixeldungeon.items.challengelists;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class CityChallenge extends ChallengeList {
	{ image = ItemSpriteSheet.CITY_CHALLENGE; }
	@Override public int challenge() { return 3; }
}
