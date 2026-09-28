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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

/** While active, ordinary enemies killed by the hero can release SPS dew. */
public class Dewcharge extends FlavourBuff {

	public static final float DURATION = 240f;

	{
		type = buffType.POSITIVE;
	}

	public boolean isDewing() {
		return true;
	}

	@Override
	public int icon() {
		return BuffIndicator.BLESS;
	}
}
