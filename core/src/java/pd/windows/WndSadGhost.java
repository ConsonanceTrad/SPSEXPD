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

public class WndSadGhost extends Window {

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
