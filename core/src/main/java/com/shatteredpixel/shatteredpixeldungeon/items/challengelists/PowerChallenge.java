package com.shatteredpixel.shatteredpixeldungeon.items.challengelists;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class PowerChallenge extends ChallengeList {
	{ image = ItemSpriteSheet.POWER_CHALLENGE; }
	@Override public int challenge() { return 6; }
}
