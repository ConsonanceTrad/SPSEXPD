/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package pd.windows;

import pd.Dungeon;
import pd.actors.mobs.npcs.Wandmaker;
import pd.items.Item;
import pd.items.wands.Wand;
import pd.messages.Messages;
import pd.scenes.PixelScene;
import pd.sprites.ItemSprite;
import pd.ui.RedButton;
import pd.ui.RenderedTextBlock;
import pd.ui.Window;

public class WndWandmaker extends Window {

	private static final int WIDTH = 120;
	private static final int BTN_HEIGHT = 20;
	private static final float GAP = 2;

	public WndWandmaker(final Wandmaker wandmaker, final Item item) {
		super();

		IconTitle titlebar = new IconTitle();
		titlebar.icon(new ItemSprite(item.image(), null));
		titlebar.label(Messages.titleCase(item.name()));
		titlebar.setRect(0, 0, WIDTH, 0);
		add(titlebar);

		RenderedTextBlock message = PixelScene.renderTextBlock(Messages.get(this, "message"), 6);
		message.maxWidth(WIDTH);
		message.setPos(0, titlebar.bottom() + GAP);
		add(message);

		RedButton btnBattle = new RedButton(Messages.get(this, "battle")) {
			@Override protected void onClick() {
				selectReward(wandmaker, item, Wandmaker.Quest.wand1);
			}
		};
		btnBattle.setRect(0, message.top() + message.height() + GAP, WIDTH, BTN_HEIGHT);
		add(btnBattle);

		RedButton btnUtility = new RedButton(Messages.get(this, "no_battle")) {
			@Override protected void onClick() {
				selectReward(wandmaker, item, Wandmaker.Quest.wand2);
			}
		};
		btnUtility.setRect(0, btnBattle.bottom() + GAP, WIDTH, BTN_HEIGHT);
		add(btnUtility);
		resize(WIDTH, (int) btnUtility.bottom());
	}

	private void selectReward(Wandmaker wandmaker, Item item, Wand reward) {
		hide();
		if (reward == null || !Dungeon.hero.belongings.contains(item)) return;

		item.detach(Dungeon.hero.belongings.backpack);
		reward.identify();
		Dungeon.level.drop(reward, wandmaker.pos).sprite.drop();
		wandmaker.yell(Messages.get(this, "farewell", Messages.titleCase(Dungeon.hero.name())));
		Dungeon.level.drop(Wandmaker.Quest.completionBonus(), wandmaker.pos).sprite.drop();
		wandmaker.destroy();
		if (wandmaker.sprite != null) wandmaker.sprite.die();
		Wandmaker.Quest.complete();
	}
}
