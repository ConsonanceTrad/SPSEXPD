/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Special Surprise Pixel Dungeon, GPLv3 or later.
 */
package pd.scenes;

import pd.messages.Messages;
import pd.windows.WndStory;
import render.noosa.Game;
import pd.messages.InlineText;

/** The one-time SPS story shown after the first hero is fully configured. */
public class IntroScene extends PixelScene {
	//SPSEXPD: inline Chinese text (generated from messages/scenes/zh)
	static {
		InlineText.of(IntroScene.class)
			.t("text", "在你之前，曾经有很多来自上方城镇的英雄向这个地城进发。有的人带回了财宝和魔法道具，而大多数人彻底销声匿迹。\n\n不过，从未有人能成功到达过底层，染指Yendor护符，传说它被深渊中的远古邪物守卫着，哪怕是现在，黑暗之力也从地下辐射而来，一路渗透到城镇中。\n\n你认为你准备好接受挑战了，更重要的是，你觉得命运女神正对你微笑。是时候开始你自己的冒险了！");
	}


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
