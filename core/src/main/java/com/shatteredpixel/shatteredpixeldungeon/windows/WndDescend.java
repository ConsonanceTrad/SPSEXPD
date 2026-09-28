/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 */

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Waterskin;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;

public class WndDescend extends WndOptions {

	public WndDescend() {
		super(new ItemSprite(new Waterskin()),
				Messages.titleCase(Messages.get(Waterskin.class, "name")),
				Messages.get(WndDescend.class, "message"),
				Messages.get(WndDescend.class, "confirm"),
				Messages.get(WndDescend.class, "cancel"));
	}

	@Override
	protected void onSelect(int index) {
		if (index == 0 && Dungeon.level != null) Dungeon.level.forceDone = true;
	}
}
