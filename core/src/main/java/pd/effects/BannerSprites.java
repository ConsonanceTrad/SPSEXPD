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

package pd.effects;

import pd.Assets;
import pd.atlas.AtlasReader;
import pd.atlas.IconEntry;
import pd.atlas.interfaces.BannersDict;
import java.util.HashMap;
import java.util.Map;
import render.noosa.Image;

public class BannerSprites {

	public enum  Type {
		TITLE_PORT,
		TITLE_GLOW_PORT,
		TITLE_LAND,
		TITLE_GLOW_LAND,
		BOSS_SLAIN,
		GAME_OVER,
	}
	//位置字典：类型 → 图集中的位置（数据源自 tools/atlas-meta，由 gen-atlas-dict 生成）
	private static final Map<Type, IconEntry> ENTRIES = new HashMap<>();
	static {
		ENTRIES.put(Type.BOSS_SLAIN, BannersDict.BANNER_004);
		ENTRIES.put(Type.GAME_OVER, BannersDict.BANNER_005);
		ENTRIES.put(Type.TITLE_GLOW_LAND, BannersDict.BANNER_003);
		ENTRIES.put(Type.TITLE_GLOW_PORT, BannersDict.BANNER_001);
		ENTRIES.put(Type.TITLE_LAND, BannersDict.BANNER_002);
		ENTRIES.put(Type.TITLE_PORT, BannersDict.BANNER_000);
	}

	public static Image get( Type type ) {
		//位置来自字典（pd/atlas），取图统一经 AtlasReader
		IconEntry entry = ENTRIES.get(type);
		if (entry != null) return AtlasReader.image(entry);
		//兜底：字典未收录该类型时返回空图
		return new Image( Assets.Interfaces.BANNERS );
	}
}
