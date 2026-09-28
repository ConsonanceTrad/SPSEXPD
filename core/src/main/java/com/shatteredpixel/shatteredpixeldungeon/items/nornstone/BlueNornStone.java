package com.shatteredpixel.shatteredpixeldungeon.items.nornstone;

import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class BlueNornStone extends NornStone {
	{
		type = 2;
		image = ItemSpriteSheet.NORN_BLUE;
	}

	@Override
	public int value() {
		return 50 * quantity();
	}
}
