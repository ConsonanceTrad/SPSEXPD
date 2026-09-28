package com.shatteredpixel.shatteredpixeldungeon.items.nornstone;

import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class PurpleNornStone extends NornStone {
	{
		type = 4;
		image = ItemSpriteSheet.NORN_PURPLE;
	}

	@Override
	public int value() {
		return 50 * quantity();
	}
}
