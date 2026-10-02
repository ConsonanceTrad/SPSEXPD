/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 */

package pd.windows;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Hunger;
import pd.actors.buffs.TentSleep;
import pd.items.Item;
import pd.items.consum.food.Food;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ItemSprite;

public class WndTent extends WndOptions {

	public WndTent() {
		super(new ItemSprite(SpecificPlaceHolderDict.SOMETHING_0),
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
