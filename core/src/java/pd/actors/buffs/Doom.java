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

import pd.sprites.CharSprite;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

public class Doom extends Buff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Doom.class)
			.t("name", "定命")
			.t("desc", "当整个宇宙都看起来想置你于死地时，继续斗争还有什么意义呢？\n\n被定命的角色受到的任何伤害都会提升67%。\n\n定命是永久性的，死后才能解脱。");
	}

	
	{
		type = buffType.NEGATIVE;
		announced = true;
	}
	
	@Override
	public void fx(boolean on) {
		if (on) target.sprite.add( CharSprite.State.DARKENED );
		else if (target.invisible == 0) target.sprite.remove( CharSprite.State.DARKENED );
	}
	
	@Override
	public int icon() {
		return BuffIndicator.CORRUPT;
	}
}
