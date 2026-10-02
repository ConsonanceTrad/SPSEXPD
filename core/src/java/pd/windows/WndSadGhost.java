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
import pd.actors.mobs.npcs.Ghost;
import pd.items.Item;
import pd.messages.Messages;
import pd.scenes.PixelScene;
import pd.sprites.FetidRatSprite;
import pd.sprites.GnollTricksterSprite;
import pd.sprites.GreatCrabSprite;
import pd.ui.RedButton;
import pd.ui.RenderedTextBlock;
import pd.ui.Window;
import pd.messages.InlineText;

public class WndSadGhost extends Window {
	//SPSEXPD: inline Chinese text (generated from messages/windows/zh)
	static {
		InlineText.of(WndSadGhost.class)
			.t("rat_title", "击败腐臭老鼠")
			.t("gnoll_title", "击败豺狼诡术师")
			.t("crab_title", "击败巨钳螃蟹")
			.t("rat", "谢谢你，那个可怕的老鼠被杀，我也终于可以安息了...不知道究竟是什么样畸形的魔法才能创造这样一个肮脏的生物...")
			.t("gnoll", "谢谢你，那个诡计多端的豺狼人被杀，我也终于可以安息了...不知道究竟是什么样畸形的魔法使它如此诡诈...")
			.t("crab", "谢谢你，那只巨蟹被杀，我也终于可以安息了...不知道究竟是什么样畸形的魔法能让它活得那么长久...")
			.t("give_item", "挑一个你喜欢的拿走吧，我再也用不着它们了...希望它们能帮助你继续走下去...\n\n还有...我在这地牢里弄丢了一件心爱之物...如果你能...找到那...玫瑰...")
			.t("weapon", "幽灵的饰品")
			.t("armor", "幽灵的信物")
			.t("pet", "幽灵的玩伴")
			.t("confirm", "确定")
			.t("cancel", "取消")
			.t("farewell", "一路顺风，冒险家！");
	}


	private static final int WIDTH		= 120;
	private static final int BTN_HEIGHT	= 20;
	private static final int GAP		= 2;

	Ghost ghost;
	
	public WndSadGhost( final Ghost ghost, final int type ) {
		
		super();

		this.ghost = ghost;
		
		IconTitle titlebar = new IconTitle();
		RenderedTextBlock message;
		switch (type){
			case 1:default:
				titlebar.icon( new FetidRatSprite() );
				titlebar.label( Messages.get(this, "rat_title") );
				message = PixelScene.renderTextBlock( Messages.get(this, "rat")+"\n\n"+Messages.get(this, "give_item"), 6 );
				break;
			case 2:
				titlebar.icon( new GnollTricksterSprite() );
				titlebar.label( Messages.get(this, "gnoll_title") );
				message = PixelScene.renderTextBlock( Messages.get(this, "gnoll")+"\n\n"+Messages.get(this, "give_item"), 6 );
				break;
			case 3:
				titlebar.icon( new GreatCrabSprite());
				titlebar.label( Messages.get(this, "crab_title") );
				message = PixelScene.renderTextBlock( Messages.get(this, "crab")+"\n\n"+Messages.get(this, "give_item"), 6 );
				break;

		}

		titlebar.setRect( 0, 0, WIDTH, 0 );
		add( titlebar );

		message.maxWidth(WIDTH);
		message.setPos(0, titlebar.bottom() + GAP);
		add( message );

		RedButton btnArtifact = new RedButton(Messages.get(this, "weapon")) {
			@Override
			protected void onClick() {
				selectReward(Ghost.Quest.artifact);
			}
		};
		btnArtifact.setRect(0, message.top() + message.height() + GAP, WIDTH, BTN_HEIGHT);
		add(btnArtifact);

		RedButton btnRing = new RedButton(Messages.get(this, "armor")) {
			@Override
			protected void onClick() {
				selectReward(Ghost.Quest.ring);
			}
		};
		btnRing.setRect(0, btnArtifact.bottom() + GAP, WIDTH, BTN_HEIGHT);
		add(btnRing);

		RedButton btnPet = new RedButton(Messages.get(this, "pet")) {
			@Override
			protected void onClick() {
				selectReward(Ghost.Quest.pet);
			}
		};
		btnPet.setRect(0, btnRing.bottom() + GAP, WIDTH, BTN_HEIGHT);
		add(btnPet);

		resize(WIDTH, (int) btnPet.bottom());
	}
	
	private void selectReward( Item reward ) {
		
		hide();
		
		if (reward == null) return;

		Dungeon.level.drop(reward, ghost.pos).sprite.drop();
		
		ghost.yell( Messages.get(this, "farewell") );
		ghost.die( null );
		
		Ghost.Quest.complete();
	}

}
