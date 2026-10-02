/*
 * Pixel Dungeon
 * Copyright (C) 2012-2014 Oleg Dolya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package pd.items.quest;

import pd.atlas.items.ConsumThrowsDict;

import pd.items.Item;

/** Retained solely for quest items present in pre-0.2.1 SPS-PD saves. */
public class RatSkull extends Item {

	{
		image = ConsumThrowsDict.SKULL;
		unique = true;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public int value() {
		return 100;
	}
}
