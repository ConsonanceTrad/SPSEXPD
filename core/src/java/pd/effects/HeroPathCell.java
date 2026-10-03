/*
 * Special Surprise Pixel Dungeon
 * Copyright (C) 2014-2021 hmdzl001
 *
 * Distributed under the GNU General Public License v3 or later.
 */

package pd.effects;

import pd.Assets;
import pd.tiles.DungeonTilemap;
import render.noosa.Image;

/**
 * SPS: 移动路径提示的单格标记。
 *
 * 与 {@link TargetedCell} 同形式：**不**加入 terrain 地图层，而是直接挂到 GameScene
 * 的独立 Group 上。之前用 DungeonTilemap 子类做，会被后加入 terrain 的
 * customTiles / customTerrain 盖住而看不见。
 *
 * 图标取 waypoint 图集：第一行 = 路径点，第二行 = 终点。
 */
public class HeroPathCell extends Image {

	/** waypoint 图集的单元格边长 */
	public static final int SIZE = 16;

	public int pos;

	public HeroPathCell(){
		super( Assets.Sprites.WAYPOINT, 0, 0, SIZE, SIZE );
	}

	/** 定位到某格；goal=true 时改用第二行的终点图标。 */
	public void reset( int pos, boolean goal ){
		frame( 0, goal ? SIZE : 0, SIZE, SIZE );
		origin.set( width/2f );
		camera = null;
		this.pos = pos;
		point( DungeonTilemap.tileToWorld( pos ) );
	}
}
