/*
 * Pixel Dungeon
 * Copyright (C) 2012-2014 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 */

package pd.items;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.hero.Hero;

import java.util.ArrayList;
import pd.messages.InlineText;

/** Commemorative flag awarded by HBB after Otiluke is rescued. */
public class Flag extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Flag.class)
			.t("name", "军旗")
			.t("desc", "祝贺中华人民共和国成立70周年。");
	}




	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
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
