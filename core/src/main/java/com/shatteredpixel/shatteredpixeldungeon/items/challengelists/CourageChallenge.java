package com.shatteredpixel.shatteredpixeldungeon.items.challengelists;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class CourageChallenge extends ChallengeList {
	{ image = ItemSpriteSheet.COURAGE_CHALLENGE; }
	@Override public int challenge() { return 5; }
}
