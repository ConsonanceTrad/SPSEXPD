/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Special Surprise Pixel Dungeon, GPLv3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndStory;
import com.watabou.noosa.Game;

/** The one-time SPS story shown after the first hero is fully configured. */
public class IntroScene extends PixelScene {

	@Override
	public void create() {
		super.create();
		add(new WndStory(Messages.get(this, "text")) {
			@Override
			public void hide() {
				super.hide();
				Game.switchScene(InterlevelScene.class);
			}
		});
		fadeIn();
	}
}
