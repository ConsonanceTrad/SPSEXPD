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

package pd.items.specific.keys;

import pd.atlas.items.SpecificKeyDict;

import pd.items.Item;

/** The chapter-exit key awarded by SPS quest givers. */
public class SpsSkeletonKey extends Key {

	{
		image = SpecificKeyDict.WORN_KEY;
		stackable = false;
	}

	public SpsSkeletonKey() {
		this(0);
	}

	public SpsSkeletonKey(int depth) {
		this.depth = depth;
	}
}
