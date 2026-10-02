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

package pd.items.consum.potions.brews;

import pd.actors.hero.Hero;
import pd.items.consum.potions.Potion;
import pd.scenes.GameScene;

import java.util.ArrayList;
import pd.messages.InlineText;

public abstract class Brew extends Potion {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Brew.class)
			.t("discover_hint", "你可通过炼金合成该物品。");
	}

	
	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions( hero );
		actions.remove( AC_DRINK );
		return actions;
	}

	@Override
	public String defaultAction() {
		return AC_THROW;
	}
	
	@Override
	public void doThrow(Hero hero) {
		GameScene.selectCell(thrower);
	}
	
	@Override
	public abstract void shatter( int cell );
	
	@Override
	public boolean isKnown() {
		return true;
	}

	@Override
	public int value() {
		return quantity * 60;
	}

	@Override
	public int energyVal() {
		return quantity * 12;
	}

}
