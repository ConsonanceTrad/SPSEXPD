package com.shatteredpixel.shatteredpixeldungeon.items.challengelists;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class IceChallenge extends ChallengeList {
	{ image = ItemSpriteSheet.ICE_CHALLENGE; }
	@Override public int challenge() { return 4; }
}
