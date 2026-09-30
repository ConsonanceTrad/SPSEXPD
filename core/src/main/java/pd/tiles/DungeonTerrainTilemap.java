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

package pd.tiles;

import pd.Dungeon;
import pd.levels.MiningLevel;
import pd.levels.Terrain;
import render.noosa.Image;

public class DungeonTerrainTilemap extends DungeonTilemap {

	static DungeonTerrainTilemap instance;

	public DungeonTerrainTilemap(){
		super(Dungeon.level.tilesTex());

		map( Dungeon.level.map, Dungeon.level.width() );

		instance = this;
	}

	@Override
	protected int getTileVisual(int pos, int tile, boolean flat) {
		//SPS: 回 2D 渲染 —— Terrain 常量经 SpsTerrainFrames 直映 SPS 帧号（单层 16x16，
		//同 SPS 0.9.8 DungeonTilemap 的“常量即帧号”语义）。2.5D 分体/缝合绘制停用。
		//水（Terrain.WATER）由 SpsWaterEdgesTilemap 独立层绘制，本层不再处理。
		return SpsTerrainFrames.visual(tile);
	}

	public static Image tile(int pos, int tile ) {
		Image img = new Image( instance.texture );
		img.frame( instance.tileset.get( instance.getTileVisual( pos, tile, true ) ) );
		return img;
	}

	@Override
	protected boolean needsRender(int pos) {
		//SPS: 水格全部交给 SpsWaterEdgesTilemap 独立层（本层不再画水）
		return super.needsRender(pos) && map[pos] != Terrain.WATER;
	}
}
