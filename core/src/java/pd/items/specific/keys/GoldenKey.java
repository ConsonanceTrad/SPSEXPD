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


public class GoldenKey extends Key {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(GoldenKey.class)
			.t("name", "金钥匙")
			.t("desc", "这把黄金钥匙的齿纹精妙而复杂。或许可以用它来打开某个上锁的宝箱？");
	}

	
	{
		image = SpecificKeyDict.GOLDEN_KEY;
	}

	public GoldenKey() {
		this( 0 );
	}
	
	public GoldenKey( int depth ) {
		super();
		this.depth = depth;
	}

}
