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

package pd.actors.buffs;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

public class LostInventory extends Buff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(LostInventory.class)
			.t("name", "遗落行囊")
			.t("desc", "你的行囊被遗落在了地牢中的某处！\n在找回你的行囊之前，你将无法拾起或使用绝大多数道具。");
	}


	{
		type = buffType.NEGATIVE;
	}

	@Override
	public boolean attachTo( Char target ) {
		if (super.attachTo( target )) {
			if (target instanceof Hero && ((Hero) target).belongings != null){
				((Hero) target).belongings.lostInventory(true);
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void detach() {
		super.detach();
		if (target instanceof Hero && ((Hero) target).belongings != null){
			((Hero) target).belongings.lostInventory(false);
		}
	}

	@Override
	public int icon() {
		return BuffIndicator.NOINV;
	}

}
