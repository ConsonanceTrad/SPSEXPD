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

package pd.items.consum.food;

import pd.atlas.items.GroundFunctionalFallingDict;

import pd.items.Item;

public class WaterItem extends Food {

	{
		image = GroundFunctionalFallingDict.DEWDROP_0;
		energy = 1f;
		hornValue = 0;
	}

	public WaterItem() {
		this(1);
	}

	public WaterItem(int number) {
		quantity = Math.max(1, number);
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
	public Item random() {
		quantity = 1;
		return this;
	}

	@Override
	public int value() {
		return quantity;
	}
}
