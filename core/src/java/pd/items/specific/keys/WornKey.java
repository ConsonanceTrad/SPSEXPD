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

package pd.items.specific.keys;

import pd.atlas.items.SpecificKeyDict;

import pd.Dungeon;
import pd.SPDSettings;
import pd.ShatteredPixelDungeon;
import pd.actors.hero.Hero;
import pd.windows.WndSupportPrompt;
import render.noosa.Game;
import render.utils.data.Callback;

import java.io.IOException;
import pd.messages.InlineText;

public class WornKey extends Key {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WornKey.class)
			.t("name", "磨损钥匙")
			.t("desc", "这把磨损而褪色的钥匙看起来非同寻常。大概它可以打开附近某道非同寻常的门。");
	}



	
	{
		image = SpecificKeyDict.WORN_KEY;
	}
	
	public WornKey() {
		this( 0 );
	}
	
	public WornKey( int depth ) {
		super();
		this.depth = depth;
	}

	@Override
	public boolean doPickUp(Hero hero, int pos) {
		if(!SPDSettings.supportNagged()){
			try {
				Dungeon.saveAll();
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						ShatteredPixelDungeon.scene().add(new WndSupportPrompt());
					}
				});
			} catch (IOException e) {
				ShatteredPixelDungeon.reportException(e);
			}
			
		}
		
		return super.doPickUp(hero, pos);
	}

}
