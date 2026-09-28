/*
 * Special Surprise Pixel Dungeon
 * Copyright (C) 2014-2021 hmdzl001
 *
 * Distributed under the GNU General Public License v3 or later.
 */

package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

import java.util.ArrayList;

public class Towel extends Item {

	public static final String AC_TOWEL = "TOWEL";

	{
		image = ItemSpriteSheet.TOWEL;
		level(20);
		defaultAction = AC_TOWEL;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_TOWEL);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (!AC_TOWEL.equals(action)) {
			super.execute(hero, action);
			return;
		}
		Buff.detach(hero, Bleeding.class);
		Buff.detach(hero, Ooze.class);
		Buff.detach(hero, Burning.class);
		Buff.detach(hero, Chill.class);
		Buff.detach(hero, Frost.class);
		GLog.i(Messages.get(this, "apply"));
		level(level() - 1);
		if (level() <= 0) {
			detach(Dungeon.hero.belongings.backpack);
			GLog.w(Messages.get(this, "end"));
		}
		hero.spendAndNext(1f);
		updateQuickslot();
	}

	@Override
	public int value() {
		return 500 * quantity;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}
}
