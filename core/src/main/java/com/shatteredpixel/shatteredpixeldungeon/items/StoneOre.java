/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 */

package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class StoneOre extends Item {

	{
		image = ItemSpriteSheet.ORE;
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
