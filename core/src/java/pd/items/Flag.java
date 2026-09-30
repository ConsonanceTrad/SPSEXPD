/*
 * Pixel Dungeon
 * Copyright (C) 2012-2014 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 */

package pd.items;

import pd.actors.hero.Hero;
import pd.sprites.ItemSpriteSheet;

import java.util.ArrayList;

/** Commemorative flag awarded by HBB after Otiluke is rescued. */
public class Flag extends Item {

	{
		image = ItemSpriteSheet.SPS_FLAG;
		stackable = false;
		unique = true;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.remove(AC_DROP);
		actions.remove(AC_THROW);
		return actions;
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
}
