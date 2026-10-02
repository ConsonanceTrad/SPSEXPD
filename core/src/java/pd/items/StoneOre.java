/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 */

package pd.items;

import pd.atlas.items.SpecificTaskDict;
import pd.messages.InlineText;


public class StoneOre extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(StoneOre.class)
			.t("name", "原石")
			.t("desc", "很普通的石头，可以用于烹饪和锻造，也可以拿去卖钱。");
	}


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
