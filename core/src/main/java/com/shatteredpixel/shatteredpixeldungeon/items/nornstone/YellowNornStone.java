package com.shatteredpixel.shatteredpixeldungeon.items.nornstone;

import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class YellowNornStone extends NornStone {
	{
		type = 5;
		image = ItemSpriteSheet.NORN_YELLOW;
	}

	@Override
	public int value() {
		return 50 * quantity();
	}
}
