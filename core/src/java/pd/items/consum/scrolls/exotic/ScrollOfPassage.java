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

package pd.items.consum.scrolls.exotic;

import pd.Dungeon;
import pd.items.consum.scrolls.ScrollOfTeleportation;
import pd.levels.Level;
import pd.levels.Transitions;
import pd.messages.Messages;
import pd.scenes.InterlevelScene;
import pd.sprites.ItemIconSheet;
import pd.utils.GLog;
import render.noosa.Game;
import pd.messages.InlineText;

public class ScrollOfPassage extends ExoticScroll {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ScrollOfPassage.class)
			.t("name", "归返秘卷")
			.t("desc", "这张羊皮纸上的咒语能瞬间将读者传送到楼上与之距离最近的区域首层。想去商店的话，用这张秘卷会非常方便。");
	}



	
	{
		icon = ItemIconSheet.SCROLL_PASSAGE;
	}
	
	@Override
	public void doRead() {

		detach(curUser.belongings.backpack);
		identify();
		readAnimation();
		
		if (!Dungeon.interfloorTeleportAllowed()) {
			
			GLog.w( Messages.get(ScrollOfTeleportation.class, "no_tele") );
			return;
			
		}

		Transitions.beforeTransition();
		InterlevelScene.mode = InterlevelScene.Mode.RETURN;
		InterlevelScene.returnDepth = Math.max(1, (Dungeon.depth - 1 - (Dungeon.depth-2)%5));
		InterlevelScene.returnBranch = 0;
		InterlevelScene.returnPos = -1;
		Game.switchScene( InterlevelScene.class );
	}
}
