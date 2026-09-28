package com.shatteredpixel.shatteredpixeldungeon.items.challengelists;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class CaveChallenge extends ChallengeList {
	{ image = ItemSpriteSheet.CAVE_CHALLENGE; }
	@Override public int challenge() { return 2; }
}
