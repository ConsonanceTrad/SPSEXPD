/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 */

package pd.items;

import pd.atlas.items.SpecificPlaceHolderDict;


public class Garbage extends Item {

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		stackable = true;
	}

	public Garbage() {
		this(1);
	}

	public Garbage(int quantity) {
		this.quantity = quantity;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public int value() {
		return 10 * quantity;
	}
}
