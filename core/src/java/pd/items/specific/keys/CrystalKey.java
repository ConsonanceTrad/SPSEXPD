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
import pd.messages.InlineText;


public class CrystalKey extends Key {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(CrystalKey.class)
			.t("name", "水晶钥匙")
			.t("desc", "这把水晶钥匙在黑暗中反射着光芒。或许可以用它来打开某样水晶制品？");
	}



	
	{
		image = SpecificKeyDict.CRYSTAL_KEY;
	}
	
	public CrystalKey() {
		this( 0 );
	}
	
	public CrystalKey( int depth ) {
		super();
		this.depth = depth;
	}
	
}
