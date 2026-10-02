/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 */

package pd.items;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
import pd.atlas.items.ConsumGoodsMaterialsMaterialsDict;


public class Garbage extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Garbage.class)
			.t("name", "垃圾")
			.t("desc", "锻造失败留下的废料。五份垃圾可以在铁砧上重新锻造。");
	}




	{
		image = ConsumGoodsMaterialsMaterialsDict.SCRAP;
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
