/*
 * Pixel Dungeon
 * Copyright (C) 2012-2014 Oleg Dolya
 *
 * Special Surprise Pixel Dungeon, GPLv3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.items;

/**
 * Exact SPS-PD save-class name for the dew container.
 *
 * The behavior lives in Waterskin so saves created during the early port remain
 * compatible with the restored legacy class name.
 */
public class DewVial extends Waterskin {

	public DewVial() {
		super();
	}

	public DewVial(int volume, int overflow) {
		super(volume, overflow);
	}
}
