/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 */

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.TentSleep;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class WndTent extends WndOptions {

	public WndTent() {
		super(new ItemSprite(ItemSpriteSheet.RATION),
				Messages.get(WndTent.class, "title"),
				Messages.get(WndTent.class, "text"),
				Messages.get(WndTent.class, "select"),
				Messages.get(WndTent.class, "cancel"));
	}

	@Override
	protected void onSelect(int index) {
		if (index == 0) GameScene.selectItem(FOOD_SELECTOR);
	}

	private static final WndBag.ItemSelector FOOD_SELECTOR = new WndBag.ItemSelector() {
		@Override
		public String textPrompt() {
			return Messages.get(WndTent.class, "select");
		}

		@Override
		public boolean itemSelectable(Item item) {
			return item instanceof Food && !item.isEquipped(Dungeon.hero);
		}

		@Override
		public void onSelect(Item item) {
			if (!(item instanceof Food) || Dungeon.hero == null || !Dungeon.hero.isAlive()) return;
			Food food = (Food)item.detach(Dungeon.hero.belongings.backpack);
			if (food == null) return;
			float time = food.energy;
			Buff.affect(Dungeon.hero, Hunger.class).satisfy(time);
			Buff.affect(Dungeon.hero, TentSleep.class, time);
			Dungeon.hero.spendAndNext(time);
		}
	};
}
