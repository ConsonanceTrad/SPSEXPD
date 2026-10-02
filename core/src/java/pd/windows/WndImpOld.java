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
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package pd.windows;

import pd.Dungeon;
import pd.actors.mobs.npcs.Imp;
import pd.items.Item;
import pd.items.quest.DwarfToken;
import pd.messages.Messages;
import pd.scenes.PixelScene;
import pd.sprites.ItemSprite;
import pd.ui.RedButton;
import pd.ui.RenderedTextBlock;
import pd.ui.Window;
import pd.utils.GLog;
import pd.messages.InlineText;

public class WndImpOld extends Window {
	//SPSEXPD: inline Chinese text (generated from messages/windows/zh)
	static {
		InlineText.of(WndImpOld.class)
			.t("message", "哦太棒了！你简直就是我的英雄！\n关于你的奖励，我现在没带钱，但我这有个更好的东西。这个戒指是我们的传家宝物，是我爷爷从一个死掉的圣骑士手上摘下来的。")
			.t("reward", "接受戒指");
	}



	
	private static final int WIDTH      = 120;
	private static final int BTN_HEIGHT = 20;
	private static final int GAP        = 2;

	public WndImpOld( final Imp imp, final DwarfToken tokens ) {
		
		super();
		
		IconTitle titlebar = new IconTitle();
		titlebar.icon( new ItemSprite( tokens.image(), null ) );
		titlebar.label( Messages.titleCase( tokens.name() ) );
		titlebar.setRect( 0, 0, WIDTH, 0 );
		add( titlebar );
		
		RenderedTextBlock message = PixelScene.renderTextBlock( Messages.get(this, "message"), 6 );
		message.maxWidth(WIDTH);
		message.setPos(0, titlebar.bottom() + GAP);
		add( message );
		
		RedButton btnReward = new RedButton( Messages.get(this, "reward") ) {
			@Override
			protected void onClick() {
				takeReward( imp, tokens, Imp.Quest.reward );
			}
		};
		btnReward.setRect( 0, message.top() + message.height() + GAP, WIDTH, BTN_HEIGHT );
		add( btnReward );
		
		resize( WIDTH, (int)btnReward.bottom() );
	}
	
	private void takeReward( Imp imp, DwarfToken tokens, Item reward ) {
		
		hide();
		
		tokens.detachAll( Dungeon.hero.belongings.backpack );
		if (reward == null) return;

		reward.identify(false);
		if (reward.doPickUp( Dungeon.hero )) {
			GLog.i( Messages.capitalize(Messages.get(Dungeon.hero, "you_now_have", reward.name())) );
		} else {
			Dungeon.level.drop( reward, imp.pos ).sprite.drop();
		}
		
		imp.flee();
		
		Imp.Quest.oldComplete();
	}
}
