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
import pd.levels.Terrain;
import pd.mechanics.pathfind.PathFinder;
import render.noosa.Image;
import render.noosa.TextureFilm;
import render.noosa.Tilemap;
import render.noosa.tweeners.AlphaTweener;
import render.utils.geom.PointF;

public abstract class DungeonTilemap extends Tilemap {

	public static final int SIZE = 16;

	protected int[] map;

	public DungeonTilemap(String tex) {
		super(tex, new TextureFilm( tex, SIZE, SIZE ) );
	}

	@Override
	//we need to retain two arrays, map is the dungeon tilemap which we can reference.
	// Data is our own internal image representation of the tiles, which may differ.
	public void map(int[] data, int cols) {
		map = data;
		super.map(new int[data.length], cols);
	}

	@Override
	public synchronized void updateMap() {
		for (int i = 0; i < data.length; i++)
			data[i] = getTileVisual(i ,map[i], false);
		super.updateMap();
	}

	@Override
	public synchronized void updateMapCell(int cell) {
		//update in a 3x3 grid to account for neighbours which might also be affected
		if (Dungeon.level.insideMap(cell)) {
			for (int i : PathFinder.NEIGHBOURS9) {
				data[cell + i] = getTileVisual(cell + i, map[cell + i], false);
			}
			super.updateMapCell(cell - mapWidth - 1);
			super.updateMapCell(cell + mapWidth + 1);

		//unless we're at the level's edge, then just do the one tile.
		} else {
			data[cell] = getTileVisual(cell, map[cell], false);
			super.updateMapCell(cell);
		}
	}

	protected abstract int getTileVisual(int pos, int tile, boolean flat);

	public int screenToTile(int x, int y ){
		return screenToTile(x, y, false);
	}

	public int screenToTile(int x, int y, boolean wallAssist ) {
		PointF p = camera().screenToCamera( x, y ).
			offset( this.point().negate() ).
			invScale( SIZE );

		// SPS-PD treats presses outside the map as no selection.
		if (p.x < 0 || p.x >= Dungeon.level.width()
				|| p.y < 0 || p.y >= Dungeon.level.height()) {
			return -1;
		}

		int cell = (int)p.x + (int)p.y * Dungeon.level.width();

		//SPS: 回 2D 渲染 —— raised 透视的点击下移补偿停用（2D 无墙面立绘偏移，
		//点击与格子严格 1:1）。wallAssist 参数保留签名兼容调用点。
		return cell;
	}

	private boolean isWallAssistable(int cell){
		if (map == null || cell >= size){
			return false;
		}

		if (DungeonTileSheet.wallStitcheable(map[cell])){
			return true;
		}

		//caves region deco is very wall-like, so it counts
		if (Dungeon.depth >= 10 && Dungeon.depth <= 15
				&& (map[cell] == Terrain.REGION_DECO || map[cell] == Terrain.REGION_DECO_ALT)) {
			return true;
		}

		return false;
	}
	
	@Override
	public boolean overlapsPoint( float x, float y ) {
		return true;
	}
	
	public void discover( int pos, int oldValue ) {
		
		int visual = getTileVisual( pos, oldValue, false);
		if (visual < 0) return;
		
		final Image tile = new Image( texture );
		tile.frame( tileset.get( getTileVisual( pos, oldValue, false)));
		tile.point( tileToWorld( pos ) );
		// Preserve the legacy bright-mode tint during the discovery fade.
		tile.rm = tile.gm = tile.bm = rm;
		tile.ra = tile.ga = tile.ba = ra;

		parent.add( tile );
		
		parent.add( new AlphaTweener( tile, 0, 0.6f ) {
			protected void onComplete() {
				tile.killAndErase();
				killAndErase();
			}
		} );
	}
	
	public static PointF tileToWorld( int pos ) {
		return new PointF( pos % Dungeon.level.width(), pos / Dungeon.level.width()  ).scale( SIZE );
	}
	
	public static PointF tileCenterToWorld( int pos ) {
		return new PointF(
			(pos % Dungeon.level.width() + 0.5f) * SIZE,
			(pos / Dungeon.level.width() + 0.5f) * SIZE );
	}

	//SPS: 回 2D 渲染 —— raised 透视锚点（y 上提到 0.1）回归普通格中心；
	//签名保留，20+ 调用点（射线/投射物/精灵）零改动
	public static PointF raisedTileCenterToWorld( int pos ) {
		return tileCenterToWorld( pos );
	}

	public static int worldToTile( float x, float y, int width){
		return (int)(x / SIZE) + ((int)(y / SIZE) * width);
	}
	
	@Override
	public boolean overlapsScreenPoint( int x, int y ) {
		return true;
	}

}
