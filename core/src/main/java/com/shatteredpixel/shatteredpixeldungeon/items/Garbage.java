/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 */

package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Garbage extends Item {

	{
		image = ItemSpriteSheet.SPS_GARBAGE;
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
