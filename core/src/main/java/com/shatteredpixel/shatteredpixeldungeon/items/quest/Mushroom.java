package com.shatteredpixel.shatteredpixeldungeon.items.quest;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Mushroom extends Item {

	{
		image = ItemSpriteSheet.MUSHROOM;
		unique = true;
	}

	@Override
	public int value() {
		//SPS: 任务蘑菇在 0 层商店固定出售，售价 10 金币（配合开局 10 金币）
		return 10;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}
}
