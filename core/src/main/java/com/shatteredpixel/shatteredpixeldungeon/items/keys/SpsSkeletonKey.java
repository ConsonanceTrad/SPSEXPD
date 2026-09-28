/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.shatteredpixel.shatteredpixeldungeon.items.keys;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

/** The chapter-exit key awarded by SPS quest givers. */
public class SpsSkeletonKey extends Key {

	{
		image = ItemSpriteSheet.WORN_KEY;
		stackable = false;
	}

	public SpsSkeletonKey() {
		this(0);
	}

	public SpsSkeletonKey(int depth) {
		this.depth = depth;
	}

	@Override
	public boolean isSimilar(Item item) {
		return false;
	}
}
