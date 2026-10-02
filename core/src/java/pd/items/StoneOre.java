/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 */

package pd.items;

import pd.atlas.items.SpecificTaskDict;


public class StoneOre extends Item {

	{
		image = SpecificTaskDict.ORE_0;
		stackable = true;
	}

	public StoneOre() {
		this(1);
	}

	public StoneOre(int quantity) {
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
		return 50 * quantity;
	}
}
