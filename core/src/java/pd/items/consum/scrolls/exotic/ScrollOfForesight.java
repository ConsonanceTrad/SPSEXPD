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

import pd.Assets;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Foresight;
import pd.sprites.ItemIconSheet;
import render.noosa.audio.Sample;
import pd.messages.InlineText;

public class ScrollOfForesight extends ExoticScroll {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ScrollOfForesight.class)
			.t("name", "先见秘卷")
			.t("desc", "在阅读了这幅卷轴后，附近地形的细节将自发不断地映入阅读者的脑海。这种效果会持续相当长的一段时间，并且会揭示所有的隐藏门与陷阱，不再需要主动搜索。");
	}

	
	{
		icon = ItemIconSheet.SCROLL_FORESIGHT;
	}
	
	@Override
	public void doRead() {

		detach(curUser.belongings.backpack);
		Sample.INSTANCE.play( Assets.Sounds.READ );
		
		Buff.affect(curUser, Foresight.class, Foresight.DURATION);

		identify();
		
		readAnimation();
	}
	
}
