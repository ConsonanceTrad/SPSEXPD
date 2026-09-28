package com.shatteredpixel.shatteredpixeldungeon.items.nornstone;

import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class GreenNornStone extends NornStone {
	{
		type = 1;
		image = ItemSpriteSheet.NORN_GREEN;
	}

	@Override
	public int value() {
		return 50 * quantity();
	}
}
