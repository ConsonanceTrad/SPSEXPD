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


public class IronKey extends Key {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(IronKey.class)
			.t("name", "铁钥匙")
			.t("desc", "这个铁钥匙的匙齿已经严重磨损；皮制系带也久经年岁摧残。它对应的是哪扇门呢?");
	}



	
	{
		image = SpecificKeyDict.IRON_KEY;
	}

	public IronKey() {
		this( 0 );
	}
	
	public IronKey( int depth ) {
		super();
		this.depth = depth;
	}

}
