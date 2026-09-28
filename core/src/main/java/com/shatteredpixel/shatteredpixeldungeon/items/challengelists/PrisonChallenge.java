package com.shatteredpixel.shatteredpixeldungeon.items.challengelists;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class PrisonChallenge extends ChallengeList {
	{ image = ItemSpriteSheet.PRISON_CHALLENGE; }
	@Override public int challenge() { return 1; }
}
