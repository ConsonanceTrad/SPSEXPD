/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Special Surprise Pixel Dungeon, GPLv3 or later.
 */
package pd.items.specific.keys;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.sprites.ItemSprite;
import pd.messages.InlineText;

/** A one-use legacy master key for locked or crystal chests on any depth. */
public class GoldenSkeletonKey extends Key {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(GoldenSkeletonKey.class)
			.t("name", "水晶钥匙")
			.t("desc", "这个水晶钥匙上的凹刻在不断地变换和移动，仿佛活的一样。或许它能用来打开某种宝箱锁？");
	}




	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
	}

	public GoldenSkeletonKey() {
		this(0);
	}

	public GoldenSkeletonKey(int depth) {
		this.depth = depth;
	}

	@Override
	public int value() {
		return 100 * quantity;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return new ItemSprite.Glowing(0xFFFFCC);
	}
}
