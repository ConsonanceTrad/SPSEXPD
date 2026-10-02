/*
 * Pixel Dungeon
 * Copyright (C) 2012-2014 Oleg Dolya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package pd.items.quest;

import pd.atlas.items.ConsumThrowsDict;

import pd.items.Item;
import pd.messages.InlineText;

/** Retained solely for quest items present in pre-0.2.1 SPS-PD saves. */
public class RatSkull extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RatSkull.class)
			.t("name", "巨鼠头骨")
			.t("desc", "一颗大得吓人的老鼠头骨。如果你能找到一面合适的墙来挂，它会是件不错的狩猎纪念品。");
	}




	{
		image = ConsumThrowsDict.SKULL;
		unique = true;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public int value() {
		return 100;
	}
}
