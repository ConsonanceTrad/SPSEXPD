/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Special Surprise Pixel Dungeon, GPLv3 or later.
 */
package pd.items.keys;

import pd.sprites.ItemSprite;
import pd.sprites.ItemSpriteSheet;

/** A one-use legacy master key for locked or crystal chests on any depth. */
public class GoldenSkeletonKey extends Key {

	{
		image = ItemSpriteSheet.GOLDEN_SKELETON_KEY;
	}

	public GoldenSkeletonKey() {
		this(0);
	}

	public GoldenSkeletonKey(int depth) {
		this.depth = depth;
	}

	@Override
	public int value() {
		return 100 * quantity;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return new ItemSprite.Glowing(0xFFFFCC);
	}
}
