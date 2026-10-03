/*
 * Special Surprise Pixel Dungeon
 * Copyright (C) 2014-2021 hmdzl001
 *
 * Distributed under the GNU General Public License v3 or later.
 */

package pd.tiles;

import pd.Assets;
import pd.Dungeon;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * SPS: 英雄移动路径提示的独立渲染层。
 *
 * 与其它渲染层不同，本层的数据源不是地形，而是自己维护的 {@code pathData}：
 *   0 = 无；1 = 路径点（waypoint 第一帧）；2 = 终点（waypoint 第二帧）。
 * {@link pd.scenes.GameScene} 在英雄每次移动后重算路径并调用 {@link #setPath}，
 * 起点固定为英雄当前格，所以已经走过的点自然消失。
 */
public class SpsHeroPathTilemap extends DungeonTilemap {

	public static final int NONE = 0;
	public static final int STEP = 1;
	public static final int GOAL = 2;

	private final int[] pathData;

	public SpsHeroPathTilemap(){
		super( Assets.Sprites.WAYPOINT );
		pathData = new int[ Dungeon.level.length() ];
		super.map( pathData, Dungeon.level.width() );
	}

	/**
	 * 用有序路径刷新显示，顺序为「英雄所在格 → 目标格」。
	 * 传 null 或空集合即隐藏整层。
	 */
	public void setPath( ArrayList<Integer> path ){
		Arrays.fill( pathData, NONE );
		if (path != null){
			for (int i = 0; i < path.size(); i++){
				int cell = path.get(i);
				if (cell < 0 || cell >= pathData.length) continue;
				pathData[cell] = (i == path.size() - 1) ? GOAL : STEP;
			}
		}
		updateMap();
	}

	public void clear(){
		setPath( null );
	}

	@Override
	protected int getTileVisual( int pos, int tile, boolean flat ){
		switch (tile){
			case STEP: return 0;   //waypoint 第一行
			case GOAL: return 1;   //waypoint 第二行
			default:   return -1;  //该格不画
		}
	}
}
