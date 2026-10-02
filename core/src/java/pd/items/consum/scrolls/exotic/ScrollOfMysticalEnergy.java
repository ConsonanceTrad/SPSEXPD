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
import pd.actors.buffs.ArtifactRecharge;
import pd.actors.buffs.Buff;
import pd.effects.SpellSprite;
import pd.items.consum.scrolls.ScrollOfRecharging;
import pd.sprites.ItemIconSheet;
import render.noosa.audio.Sample;
import pd.messages.InlineText;

public class ScrollOfMysticalEnergy extends ExoticScroll {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ScrollOfMysticalEnergy.class)
			.t("name", "魔能秘卷")
			.t("desc", "奇异的魔法能量被禁锢在秘卷羊皮纸内，当这股能量被释放时会在短时间内持续为阅读者的所有神器充能。");
	}



	
	{
		icon = ItemIconSheet.SCROLL_MYSTENRG;
	}
	
	@Override
	public void doRead() {

		detach(curUser.belongings.backpack);
		//append buff
		Buff.affect(curUser, ArtifactRecharge.class).set( 30 ).ignoreHornOfPlenty = false;

		Sample.INSTANCE.play( Assets.Sounds.READ );
		Sample.INSTANCE.play( Assets.Sounds.CHARGEUP );
		
		SpellSprite.show( curUser, SpellSprite.CHARGE, 0, 1, 1 );
		identify();
		ScrollOfRecharging.charge(curUser);
		
		readAnimation();
	}
	
}
