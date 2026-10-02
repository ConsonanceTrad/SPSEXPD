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

import pd.actors.buffs.Buff;
import pd.actors.buffs.MagicImmune;
import pd.effects.Flare;
import pd.sprites.ItemIconSheet;
import pd.messages.InlineText;

public class ScrollOfAntiMagic extends ExoticScroll {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ScrollOfAntiMagic.class)
			.t("name", "驱魔秘卷")
			.t("desc", "使用这张秘卷会让你被包裹在一个能够屏蔽所有魔法效果的魔力结界中，无论它是有利或是有害。屏蔽效果包括大多数魔法物品效果，例如法杖、卷轴、戒指、神器、附魔与诅咒。特别地，英雄护甲技能足够强大，因而能够不受该秘卷的限制。");
	}



	
	{
		icon = ItemIconSheet.SCROLL_ANTIMAGIC;
	}
	
	@Override
	public void doRead() {

		detach(curUser.belongings.backpack);
		Buff.affect( curUser, MagicImmune.class, MagicImmune.DURATION );
		new Flare( 5, 32 ).color( 0x00FF00, true ).show( curUser.sprite, 2f );

		identify();
		
		readAnimation();
	}
}
