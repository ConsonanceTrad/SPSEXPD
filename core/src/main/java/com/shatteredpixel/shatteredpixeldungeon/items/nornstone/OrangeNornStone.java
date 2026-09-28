package com.shatteredpixel.shatteredpixeldungeon.items.nornstone;

import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class OrangeNornStone extends NornStone {
	{
		type = 3;
		image = ItemSpriteSheet.NORN_ORANGE;
	}

	@Override
	public int value() {
		return 50 * quantity();
	}
}
